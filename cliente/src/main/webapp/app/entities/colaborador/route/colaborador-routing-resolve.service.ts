import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IColaborador } from '../colaborador.model';
import { ColaboradorService } from '../service/colaborador.service';

export const colaboradorResolve = (route: ActivatedRouteSnapshot): Observable<null | IColaborador> => {
  const id = route.params['id'];
  if (id) {
    return inject(ColaboradorService)
      .find(id)
      .pipe(
        mergeMap((colaborador: HttpResponse<IColaborador>) => {
          if (colaborador.body) {
            return of(colaborador.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default colaboradorResolve;
