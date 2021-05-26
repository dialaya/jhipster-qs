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

describe('Member e2e test', () => {
  let startingEntitiesCount = 0;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });

    cy.clearCookies();
    cy.intercept('GET', '/api/members*').as('entitiesRequest');
    cy.visit('');
    cy.login('admin', 'admin');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  it('should load Members', () => {
    cy.intercept('GET', '/api/members*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Member').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Member page', () => {
    cy.intercept('GET', '/api/members*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('member');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Member page', () => {
    cy.intercept('GET', '/api/members*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Member');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Member page', () => {
    cy.intercept('GET', '/api/members*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Member');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of Member', () => {
    cy.intercept('GET', '/api/members*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Member');

    cy.get(`[data-cy="title"]`).type('SSL', { force: true }).invoke('val').should('match', new RegExp('SSL'));

    cy.get(`[data-cy="firstName"]`).type('Étienne', { force: true }).invoke('val').should('match', new RegExp('Étienne'));

    cy.get(`[data-cy="lastName"]`).type('Maillard', { force: true }).invoke('val').should('match', new RegExp('Maillard'));

    cy.get(`[data-cy="email"]`)
      .type('Lucas_Olivier@gmail.com', { force: true })
      .invoke('val')
      .should('match', new RegExp('Lucas_Olivier@gmail.com'));

    cy.get(`[data-cy="phoneNumber"]`)
      .type('Games global matrix', { force: true })
      .invoke('val')
      .should('match', new RegExp('Games global matrix'));

    cy.get(`[data-cy="requestDate"]`).type('2021-05-25T09:39').invoke('val').should('equal', '2021-05-25T09:39');

    cy.setFieldSelectToLastOfEntity('location');

    cy.setFieldSelectToLastOfEntity('legalRepresentative');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/members*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of Member', () => {
    cy.intercept('GET', '/api/members*').as('entitiesRequest');
    cy.intercept('DELETE', '/api/members/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('member');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.getEntityDeleteDialogHeading('member').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/members*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('member');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
