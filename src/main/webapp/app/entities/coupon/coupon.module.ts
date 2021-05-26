import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { CouponComponent } from './list/coupon.component';
import { CouponDetailComponent } from './detail/coupon-detail.component';
import { CouponUpdateComponent } from './update/coupon-update.component';
import { CouponDeleteDialogComponent } from './delete/coupon-delete-dialog.component';
import { CouponRoutingModule } from './route/coupon-routing.module';

@NgModule({
  imports: [SharedModule, CouponRoutingModule],
  declarations: [CouponComponent, CouponDetailComponent, CouponUpdateComponent, CouponDeleteDialogComponent],
  entryComponents: [CouponDeleteDialogComponent],
})
export class CouponModule {}
