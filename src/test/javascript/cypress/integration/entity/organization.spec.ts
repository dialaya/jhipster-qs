import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Organization e2e test', () => {
  let startingEntitiesCount = 0;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });

    cy.clearCookies();
    cy.intercept('GET', '/api/organizations*').as('entitiesRequest');
    cy.visit('');
    cy.login('admin', 'admin');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  it('should load Organizations', () => {
    cy.intercept('GET', '/api/organizations*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Organization').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Organization page', () => {
    cy.intercept('GET', '/api/organizations*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('organization');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Organization page', () => {
    cy.intercept('GET', '/api/organizations*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Organization');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Organization page', () => {
    cy.intercept('GET', '/api/organizations*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Organization');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of Organization', () => {
    cy.intercept('GET', '/api/organizations*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Organization');

    cy.get(`[data-cy="name"]`)
      .type('open-source Buckinghamshire', { force: true })
      .invoke('val')
      .should('match', new RegExp('open-source Buckinghamshire'));

    cy.get(`[data-cy="description"]`)
      .type('Borders Rhône-Alpes', { force: true })
      .invoke('val')
      .should('match', new RegExp('Borders Rhône-Alpes'));

    cy.get(`[data-cy="baseRate"]`).type('65909').should('have.value', '65909');

    cy.get(`[data-cy="groupRate"]`).type('63203').should('have.value', '63203');

    cy.get(`[data-cy="fullRate"]`).type('27779').should('have.value', '27779');

    cy.setFieldSelectToLastOfEntity('location');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/organizations*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of Organization', () => {
    cy.intercept('GET', '/api/organizations*').as('entitiesRequest');
    cy.intercept('DELETE', '/api/organizations/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('organization');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.getEntityDeleteDialogHeading('organization').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/organizations*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('organization');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
