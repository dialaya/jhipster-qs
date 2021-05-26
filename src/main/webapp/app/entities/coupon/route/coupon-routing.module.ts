import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CouponComponent } from '../list/coupon.component';
import { CouponDetailComponent } from '../detail/coupon-detail.component';
import { CouponUpdateComponent } from '../update/coupon-update.component';
import { CouponRoutingResolveService } from './coupon-routing-resolve.service';

const couponRoute: Routes = [
  {
    path: '',
    component: CouponComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CouponDetailComponent,
    resolve: {
      coupon: CouponRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CouponUpdateComponent,
    resolve: {
      coupon: CouponRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CouponUpdateComponent,
    resolve: {
      coupon: CouponRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(couponRoute)],
  exports: [RouterModule],
})
export class CouponRoutingModule {}
