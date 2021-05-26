import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'region',
        data: { pageTitle: 'jhMonoApp.region.home.title' },
        loadChildren: () => import('./region/region.module').then(m => m.RegionModule),
      },
      {
        path: 'country',
        data: { pageTitle: 'jhMonoApp.country.home.title' },
        loadChildren: () => import('./country/country.module').then(m => m.CountryModule),
      },
      {
        path: 'location',
        data: { pageTitle: 'jhMonoApp.location.home.title' },
        loadChildren: () => import('./location/location.module').then(m => m.LocationModule),
      },
      {
        path: 'member',
        data: { pageTitle: 'jhMonoApp.member.home.title' },
        loadChildren: () => import('./member/member.module').then(m => m.MemberModule),
      },
      {
        path: 'organization',
        data: { pageTitle: 'jhMonoApp.organization.home.title' },
        loadChildren: () => import('./organization/organization.module').then(m => m.OrganizationModule),
      },
      {
        path: 'membership',
        data: { pageTitle: 'jhMonoApp.membership.home.title' },
        loadChildren: () => import('./membership/membership.module').then(m => m.MembershipModule),
      },
      {
        path: 'order',
        data: { pageTitle: 'jhMonoApp.order.home.title' },
        loadChildren: () => import('./order/order.module').then(m => m.OrderModule),
      },
      {
        path: 'coupon',
        data: { pageTitle: 'jhMonoApp.coupon.home.title' },
        loadChildren: () => import('./coupon/coupon.module').then(m => m.CouponModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
