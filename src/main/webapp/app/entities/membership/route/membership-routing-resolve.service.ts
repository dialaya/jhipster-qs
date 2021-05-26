import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMembership, Membership } from '../membership.model';
import { MembershipService } from '../service/membership.service';

@Injectable({ providedIn: 'root' })
export class MembershipRoutingResolveService implements Resolve<IMembership> {
  constructor(protected service: MembershipService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMembership> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((membership: HttpResponse<Membership>) => {
          if (membership.body) {
            return of(membership.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Membership());
  }
}
