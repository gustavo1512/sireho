import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITipoCargo } from '../tipo-cargo.model';
import { TipoCargoService } from '../service/tipo-cargo.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './tipo-cargo-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TipoCargoDeleteDialogComponent {
  tipoCargo?: ITipoCargo;

  constructor(protected tipoCargoService: TipoCargoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tipoCargoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
