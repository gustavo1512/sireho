import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITipoCargo } from '../tipo-cargo.model';
import { TipoCargoService } from '../service/tipo-cargo.service';

export const tipoCargoResolve = (route: ActivatedRouteSnapshot): Observable<null | ITipoCargo> => {
  const id = route.params['id'];
  if (id) {
    return inject(TipoCargoService)
      .find(id)
      .pipe(
        mergeMap((tipoCargo: HttpResponse<ITipoCargo>) => {
          if (tipoCargo.body) {
            return of(tipoCargo.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default tipoCargoResolve;
