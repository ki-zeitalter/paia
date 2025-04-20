import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TodoService, Todo, StatusUpdate } from '../../services/todo.service';

@Component({
  selector: 'app-todo-widget',
  standalone: true,
  imports: [
    CommonModule, 
    MatCardModule, 
    MatTableModule, 
    MatIconModule, 
    MatButtonModule,
    MatTooltipModule
  ],
  template: `
    <div class="todo-widget">
      <div class="table-container">
        <table mat-table [dataSource]="todos" class="todo-table">

          <!-- Name Spalte -->
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Aufgabe</th>
            <td mat-cell *matCellDef="let task">{{ task.name }}</td>
          </ng-container>

          <!-- Priorität Spalte -->
          <ng-container matColumnDef="priority">
            <th mat-header-cell *matHeaderCellDef>Priorität</th>
            <td mat-cell *matCellDef="let task">
              <span [ngClass]="getPriorityClass(task.priority)">
                {{ getPriorityText(task.priority) }}
              </span>
            </td>
          </ng-container>

          <!-- Fälligkeitsdatum Spalte -->
          <ng-container matColumnDef="dueDate">
            <th mat-header-cell *matHeaderCellDef>Fällig</th>
            <td mat-cell *matCellDef="let task">
              {{ formatDate(task.dueDate) }}
            </td>
          </ng-container>

          <!-- Status Spalte -->
          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef>Status</th>
            <td mat-cell *matCellDef="let task">
              <span [ngClass]="getStatusClass(task.status)">
                {{ getStatusText(task.status) }}
              </span>
            </td>
          </ng-container>

          <!-- Aktionen Spalte -->
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef>Aktionen</th>
            <td mat-cell *matCellDef="let task">
              <button mat-icon-button 
                      matTooltip="Erledigt"
                      (click)="markAsDone(task)">
                <mat-icon>done</mat-icon>
              </button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
        </table>

        <div *ngIf="todos.length === 0" class="no-tasks">
          Keine anstehenden Aufgaben.
        </div>
      </div>
    </div>
  `,
  styles: [`
    .todo-widget {
      display: flex;
      flex-direction: column;
      height: 100%;
      padding: 8px;
      overflow: hidden;
    }
    
    .table-container {
      overflow: auto;
      flex: 1;
    }
    
    .todo-table {
      width: 100%;
    }
    
    .no-tasks {
      display: flex;
      height: 100%;
      justify-content: center;
      align-items: center;
      padding: 20px;
      color: #666;
    }
    
    .priority-high {
      color: #f44336;
      font-weight: bold;
    }
    
    .priority-medium {
      color: #ff9800;
    }
    
    .priority-low {
      color: #4caf50;
    }
    
    .status-open {
      padding: 2px 6px;
      background-color: #e3f2fd;
      border-radius: 4px;
    }
    
    .status-in-progress {
      padding: 2px 6px;
      background-color: #fff9c4;
      border-radius: 4px;
    }
    
    .status-done {
      padding: 2px 6px;
      background-color: #e8f5e9;
      border-radius: 4px;
      text-decoration: line-through;
    }
  `]
})
export class TodoWidgetComponent implements OnInit {
  todos: Todo[] = [];
  displayedColumns: string[] = ['name', 'priority', 'dueDate', 'status', 'actions'];

  constructor(private todoService: TodoService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.todoService.getTodos().subscribe(
      (data) => {
        this.todos = data;
      },
      (error) => {
        console.error('Fehler beim Laden der Aufgaben:', error);
      }
    );
  }

  markAsDone(task: Todo): void {
    const statusUpdate: StatusUpdate = {
      status: 'DONE'
    };
    
    this.todoService.updateTodoStatus(task.id, statusUpdate)
      .subscribe(
        () => {
          this.loadTasks(); // Aufgaben neu laden
        },
        (error) => {
          console.error('Fehler beim Aktualisieren des Status:', error);
        }
      );
  }

  formatDate(dateString: string | null): string {
    if (!dateString) return '-';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  getPriorityClass(priority: string | null): string {
    if (!priority) return '';
    
    switch (priority) {
      case 'HIGH': return 'priority-high';
      case 'MEDIUM': return 'priority-medium';
      case 'LOW': return 'priority-low';
      default: return '';
    }
  }

  getPriorityText(priority: string | null): string {
    if (!priority) return '-';
    
    switch (priority) {
      case 'HIGH': return 'Hoch';
      case 'MEDIUM': return 'Mittel';
      case 'LOW': return 'Niedrig';
      default: return priority;
    }
  }

  getStatusClass(status: string | null): string {
    if (!status) return '';
    
    switch (status) {
      case 'OPEN': return 'status-open';
      case 'IN_PROGRESS': return 'status-in-progress';
      case 'DONE': return 'status-done';
      default: return '';
    }
  }

  getStatusText(status: string | null): string {
    if (!status) return '-';
    
    switch (status) {
      case 'OPEN': return 'Offen';
      case 'IN_PROGRESS': return 'In Bearbeitung';
      case 'DONE': return 'Erledigt';
      case 'DRAFT': return 'Entwurf';
      default: return status;
    }
  }
} 