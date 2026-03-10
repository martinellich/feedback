# Use Case: Create Form from Template

## Overview

**Use Case ID:** UC-13
**Use Case Name:** Create Form from Template
**Primary Actor:** User
**Goal:** Create a new feedback form pre-populated with questions from a template
**Status:** Implemented

## Preconditions

- User is authenticated
- User owns at least one template

## Main Success Scenario

1. User clicks "Create from Template" in the dashboard
2. System displays a dialog with a template selector and form fields (title, speaker, date, location)
3. User selects a template and fills in the form details
4. System creates a new DRAFT form with the template's questions and a new publicToken
5. Dashboard is updated with the new form

## Alternative Flows

### 3a. User does not enter a title

1. System shows validation error: title is required
2. User enters a valid title
3. Flow continues at step 4

### 3b. User cancels the dialog

1. No form is created
2. Flow ends

## Postconditions

### Success Postconditions

- A new form in DRAFT status is created with a unique publicToken
- Form contains copies of the template's questions (text, type, order)
- Form is independent of the template (no foreign key link)

### Failure Postconditions

- No form is created

## Business Rules

### BR-027: Form Is Independent

The created form is independent of the template — no foreign key relationship exists

### BR-028: Starts in DRAFT

Forms created from templates always start in DRAFT status
