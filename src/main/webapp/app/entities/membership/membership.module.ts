import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { MembershipComponent } from './list/membership.component';
import { MembershipDetailComponent } from './detail/membership-detail.component';
import { MembershipUpdateComponent } from './update/membership-update.component';
import { MembershipDeleteDialogComponent } from './delete/membership-delete-dialog.component';
import { MembershipRoutingModule } from './route/membership-routing.module';

@NgModule({
  imports: [SharedModule, MembershipRoutingModule],
  declarations: [MembershipComponent, MembershipDetailComponent, MembershipUpdateComponent, MembershipDeleteDialogComponent],
  entryComponents: [MembershipDeleteDialogComponent],
})
export class MembershipModule {}
