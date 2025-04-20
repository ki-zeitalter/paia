import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Todo {
  id: number;
  name: string;
  description: string;
  userName: string;
  status: string;
  priority: string;
  dueDate: string;
  reminders?: number[];
}

export interface StatusUpdate {
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Ruft alle Todos des aktuellen Benutzers ab
  getTodos(): Observable<Todo[]> {
    return this.http.get<Todo[]>(`${this.apiUrl}/todos`);
  }

  // Ruft ein bestimmtes Todo anhand seiner ID ab
  getTodoById(id: number): Observable<Todo> {
    return this.http.get<Todo>(`${this.apiUrl}/todos/${id}`);
  }

  // Erstellt ein neues Todo
  createTodo(todo: Todo): Observable<Todo> {
    return this.http.post<Todo>(`${this.apiUrl}/todos`, todo);
  }

  // Aktualisiert ein bestehendes Todo
  updateTodo(id: number, todo: Todo): Observable<Todo> {
    return this.http.put<Todo>(`${this.apiUrl}/todos/${id}`, todo);
  }

  // Aktualisiert nur den Status eines Todos
  updateTodoStatus(id: number, statusUpdate: StatusUpdate): Observable<Todo> {
    return this.http.patch<Todo>(`${this.apiUrl}/todos/${id}/status`, statusUpdate);
  }

  // Löscht ein Todo
  deleteTodo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/todos/${id}`);
  }
} 