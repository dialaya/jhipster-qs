import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrganization } from '../organization.model';
import { OrganizationService } from '../service/organization.service';
import { OrganizationDeleteDialogComponent } from '../delete/organization-delete-dialog.component';

@Component({
  selector: 'jhi-organization',
  templateUrl: './organization.component.html',
})
export class OrganizationComponent implements OnInit {
  organizations?: IOrganization[];
  isLoading = false;

  constructor(protected organizationService: OrganizationService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.organizationService.query().subscribe(
      (res: HttpResponse<IOrganization[]>) => {
        this.isLoading = false;
        this.organizations = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IOrganization): number {
    return item.id!;
  }

  delete(organization: IOrganization): void {
    const modalRef = this.modalService.open(OrganizationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.organization = organization;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
