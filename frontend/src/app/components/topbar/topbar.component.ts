import { Component, Input } from '@angular/core';
import { IonicModule } from '@ionic/angular';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [IonicModule],
  template: `
    <div class="topbar">
      <h3>{{ title }}</h3>
      <div class="topbar-right">
        <span class="user-name">{{ userName }}</span>
      </div>
    </div>
  `,
  styles: [`
    .topbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 24px;
      background: white;
      border-bottom: 1px solid #e0e0e0;
    }
    .topbar h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #1d3557;
    }
    .topbar-right {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    .user-name {
      font-size: 14px;
      color: #6c757d;
    }
  `]
})
export class TopbarComponent {
  @Input() title = '';
  @Input() userName = '';
}
