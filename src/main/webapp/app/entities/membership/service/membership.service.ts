import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMembership, getMembershipIdentifier } from '../membership.model';

export type EntityResponseType = HttpResponse<IMembership>;
export type EntityArrayResponseType = HttpResponse<IMembership[]>;

@Injectable({ providedIn: 'root' })
export class MembershipService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/memberships');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(membership: IMembership): Observable<EntityResponseType> {
    return this.http.post<IMembership>(this.resourceUrl, membership, { observe: 'response' });
  }

  update(membership: IMembership): Observable<EntityResponseType> {
    return this.http.put<IMembership>(`${this.resourceUrl}/${getMembershipIdentifier(membership) as number}`, membership, {
      observe: 'response',
    });
  }

  partialUpdate(membership: IMembership): Observable<EntityResponseType> {
    return this.http.patch<IMembership>(`${this.resourceUrl}/${getMembershipIdentifier(membership) as number}`, membership, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMembership>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMembership[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMembershipToCollectionIfMissing(
    membershipCollection: IMembership[],
    ...membershipsToCheck: (IMembership | null | undefined)[]
  ): IMembership[] {
    const memberships: IMembership[] = membershipsToCheck.filter(isPresent);
    if (memberships.length > 0) {
      const membershipCollectionIdentifiers = membershipCollection.map(membershipItem => getMembershipIdentifier(membershipItem)!);
      const membershipsToAdd = memberships.filter(membershipItem => {
        const membershipIdentifier = getMembershipIdentifier(membershipItem);
        if (membershipIdentifier == null || membershipCollectionIdentifiers.includes(membershipIdentifier)) {
          return false;
        }
        membershipCollectionIdentifiers.push(membershipIdentifier);
        return true;
      });
      return [...membershipsToAdd, ...membershipCollection];
    }
    return membershipCollection;
  }
}
