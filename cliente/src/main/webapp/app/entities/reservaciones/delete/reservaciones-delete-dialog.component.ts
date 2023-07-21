import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IReservaciones } from '../reservaciones.model';
import { ReservacionesService } from '../service/reservaciones.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './reservaciones-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReservacionesDeleteDialogComponent {
  reservaciones?: IReservaciones;

  constructor(protected reservacionesService: ReservacionesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reservacionesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
