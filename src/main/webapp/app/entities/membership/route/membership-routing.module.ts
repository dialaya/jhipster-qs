import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MembershipComponent } from '../list/membership.component';
import { MembershipDetailComponent } from '../detail/membership-detail.component';
import { MembershipUpdateComponent } from '../update/membership-update.component';
import { MembershipRoutingResolveService } from './membership-routing-resolve.service';

const membershipRoute: Routes = [
  {
    path: '',
    component: MembershipComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MembershipDetailComponent,
    resolve: {
      membership: MembershipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MembershipUpdateComponent,
    resolve: {
      membership: MembershipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MembershipUpdateComponent,
    resolve: {
      membership: MembershipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(membershipRoute)],
  exports: [RouterModule],
})
export class MembershipRoutingModule {}
