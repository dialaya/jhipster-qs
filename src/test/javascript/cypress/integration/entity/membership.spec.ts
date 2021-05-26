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

describe('Membership e2e test', () => {
  let startingEntitiesCount = 0;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });

    cy.clearCookies();
    cy.intercept('GET', '/api/memberships*').as('entitiesRequest');
    cy.visit('');
    cy.login('admin', 'admin');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  it('should load Memberships', () => {
    cy.intercept('GET', '/api/memberships*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Membership').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Membership page', () => {
    cy.intercept('GET', '/api/memberships*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('membership');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Membership page', () => {
    cy.intercept('GET', '/api/memberships*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Membership');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Membership page', () => {
    cy.intercept('GET', '/api/memberships*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Membership');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of Membership', () => {
    cy.intercept('GET', '/api/memberships*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Membership');

    cy.get(`[data-cy="memberEmail"]`)
      .type('connect Granite Wooden', { force: true })
      .invoke('val')
      .should('match', new RegExp('connect Granite Wooden'));

    cy.get(`[data-cy="organisationName"]`)
      .type('Health systems', { force: true })
      .invoke('val')
      .should('match', new RegExp('Health systems'));

    cy.setFieldSelectToLastOfEntity('organization');

    cy.setFieldSelectToLastOfEntity('member');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/memberships*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of Membership', () => {
    cy.intercept('GET', '/api/memberships*').as('entitiesRequest');
    cy.intercept('DELETE', '/api/memberships/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('membership');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.getEntityDeleteDialogHeading('membership').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/memberships*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('membership');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
