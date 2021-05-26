import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMembership } from '../membership.model';

@Component({
  selector: 'jhi-membership-detail',
  templateUrl: './membership-detail.component.html',
})
export class MembershipDetailComponent implements OnInit {
  membership: IMembership | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ membership }) => {
      this.membership = membership;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
