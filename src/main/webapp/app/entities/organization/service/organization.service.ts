import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrganization, getOrganizationIdentifier } from '../organization.model';

export type EntityResponseType = HttpResponse<IOrganization>;
export type EntityArrayResponseType = HttpResponse<IOrganization[]>;

@Injectable({ providedIn: 'root' })
export class OrganizationService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/organizations');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(organization: IOrganization): Observable<EntityResponseType> {
    return this.http.post<IOrganization>(this.resourceUrl, organization, { observe: 'response' });
  }

  update(organization: IOrganization): Observable<EntityResponseType> {
    return this.http.put<IOrganization>(`${this.resourceUrl}/${getOrganizationIdentifier(organization) as number}`, organization, {
      observe: 'response',
    });
  }

  partialUpdate(organization: IOrganization): Observable<EntityResponseType> {
    return this.http.patch<IOrganization>(`${this.resourceUrl}/${getOrganizationIdentifier(organization) as number}`, organization, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOrganization>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrganization[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOrganizationToCollectionIfMissing(
    organizationCollection: IOrganization[],
    ...organizationsToCheck: (IOrganization | null | undefined)[]
  ): IOrganization[] {
    const organizations: IOrganization[] = organizationsToCheck.filter(isPresent);
    if (organizations.length > 0) {
      const organizationCollectionIdentifiers = organizationCollection.map(
        organizationItem => getOrganizationIdentifier(organizationItem)!
      );
      const organizationsToAdd = organizations.filter(organizationItem => {
        const organizationIdentifier = getOrganizationIdentifier(organizationItem);
        if (organizationIdentifier == null || organizationCollectionIdentifiers.includes(organizationIdentifier)) {
          return false;
        }
        organizationCollectionIdentifiers.push(organizationIdentifier);
        return true;
      });
      return [...organizationsToAdd, ...organizationCollection];
    }
    return organizationCollection;
  }
}
