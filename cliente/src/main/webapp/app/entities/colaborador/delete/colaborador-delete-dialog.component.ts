import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IColaborador } from '../colaborador.model';
import { ColaboradorService } from '../service/colaborador.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './colaborador-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ColaboradorDeleteDialogComponent {
  colaborador?: IColaborador;

  constructor(protected colaboradorService: ColaboradorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.colaboradorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
