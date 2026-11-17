import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

export interface DocumentItem {
  id?: string;
  title: string;
  description: string;
  classificationLevel: string;
  department: string;
  owner?: string;
  tags?: string[];
  createdAt?: string;
}

export interface AuditLog {
  id?: string;
  username: string;
  action: string;
  resource: string;
  resourceId?: string;
  httpMethod: string;
  clientIp: string;
  statusCode: number;
  details: string;
  timestamp: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  apiUrl = 'http://localhost:8080/api/v1';

  // Auth credentials
  username = 'admin';
  password = 'admin123';
  authStatus = 'Authenticated as admin (Role: ADMIN, USER)';

  // View state
  activeTab: 'documents' | 'audit' = 'documents';
  statusMessage = '';
  isError = false;

  // Data lists
  documents: DocumentItem[] = [];
  auditLogs: AuditLog[] = [];

  // New Document Model
  newDoc: DocumentItem = {
    title: '',
    description: '',
    classificationLevel: 'INTERNAL',
    department: 'Cybersecurity',
    tags: ['policy']
  };
  tagsInput = 'policy';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchDocuments();
  }

  getAuthHeaders(): HttpHeaders {
    const token = btoa(`${this.username}:${this.password}`);
    return new HttpHeaders({
      'Authorization': `Basic ${token}`,
      'Content-Type': 'application/json'
    });
  }

  setCredentials(user: string, pass: string, roleName: string) {
    this.username = user;
    this.password = pass;
    this.authStatus = `Switched to: ${user} (${roleName})`;
    this.fetchDocuments();
  }

  fetchDocuments() {
    this.http.get<DocumentItem[]>(`${this.apiUrl}/documents`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (data) => {
          this.documents = data;
          this.statusMessage = `HTTP 200 OK: Loaded ${data.length} documents`;
          this.isError = false;
        },
        error: (err) => {
          this.statusMessage = `HTTP ${err.status}: ${err.error?.message || err.message}`;
          this.isError = true;
          this.documents = [];
        }
      });
  }

  createDocument() {
    if (!this.newDoc.title.trim()) {
      alert('Please provide a document title');
      return;
    }
    this.newDoc.tags = this.tagsInput.split(',').map(t => t.trim()).filter(Boolean);

    this.http.post<DocumentItem>(`${this.apiUrl}/documents`, this.newDoc, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (created) => {
          this.statusMessage = `HTTP 201 Created: "${created.title}" [ID: ${created.id}]`;
          this.isError = false;
          this.newDoc.title = '';
          this.newDoc.description = '';
          this.fetchDocuments();
        },
        error: (err) => {
          this.statusMessage = `HTTP ${err.status} Error: ${err.error?.message || err.message}`;
          this.isError = true;
        }
      });
  }

  deleteDocument(id: string | undefined) {
    if (!id) return;
    if (!confirm('Are you sure you want to delete this document? (Requires ADMIN role)')) return;

    this.http.delete<void>(`${this.apiUrl}/documents/${id}`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: () => {
          this.statusMessage = `HTTP 204 No Content: Document ${id} deleted`;
          this.isError = false;
          this.fetchDocuments();
        },
        error: (err) => {
          this.statusMessage = `HTTP ${err.status} Forbidden: Only users with ROLE_ADMIN can delete documents`;
          this.isError = true;
        }
      });
  }

  fetchAuditLogs() {
    this.activeTab = 'audit';
    this.http.get<AuditLog[]>(`${this.apiUrl}/audit-logs`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (logs) => {
          this.auditLogs = logs;
          this.statusMessage = `HTTP 200 OK: Audit trail loaded (${logs.length} events)`;
          this.isError = false;
        },
        error: (err) => {
          this.statusMessage = `HTTP ${err.status} Forbidden: Access to Audit logs requires ROLE_ADMIN`;
          this.isError = true;
          this.auditLogs = [];
        }
      });
  }
}
