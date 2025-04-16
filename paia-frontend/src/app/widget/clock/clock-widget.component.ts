import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { interval, Subscription } from 'rxjs';
import { WidgetComponent } from '../widget.component';
import { Widget } from '../../models/widget';

@Component({
  selector: 'app-clock-widget',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="clock-widget">
      <div class="time">{{ currentTime }}</div>
      <div class="date">{{ currentDate }}</div>
    </div>
  `,
  styles: [`
    .clock-widget {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      padding: 16px;
    }
    .time {
      font-size: 3rem;
      font-weight: bold;
    }
    .date {
      font-size: 1.2rem;
      margin-top: 8px;
    }
  `]
})
export class ClockWidgetComponent implements OnInit, OnDestroy {
  currentTime: string = '';
  currentDate: string = '';
  private timeSubscription?: Subscription;

  ngOnInit(): void {
    // Aktualisiere die Zeit jede Sekunde
    this.updateTime();
    this.timeSubscription = interval(1000).subscribe(() => {
      this.updateTime();
    });
  }

  ngOnDestroy(): void {
    if (this.timeSubscription) {
      this.timeSubscription.unsubscribe();
    }
  }

  private updateTime(): void {
    const now = new Date();
    this.currentTime = now.toLocaleTimeString('de-DE');
    this.currentDate = now.toLocaleDateString('de-DE', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
} 