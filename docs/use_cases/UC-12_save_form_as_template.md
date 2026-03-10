# Use Case: Save Form as Template

## Overview

**Use Case ID:** UC-12
**Use Case Name:** Save Form as Template
**Primary Actor:** Form Owner
**Goal:** Save a form's question structure as a reusable template
**Status:** Implemented

## Preconditions

- User is authenticated
- User is the owner of the form

## Main Success Scenario

1. User clicks "Save as Template" on a form in the dashboard
2. System displays a dialog with a template name field, pre-filled with the form title
3. User confirms or edits the template name
4. System creates a new template with the form's questions (text, type, order)
5. System confirms the template was saved

## Alternative Flows

### 3a. User clears the template name

1. System shows validation error: template name is required
2. User enters a valid name
3. Flow continues at step 4

### 3b. User cancels the dialog

1. No template is created
2. Flow ends

## Postconditions

### Success Postconditions

- A new template is created, owned by the current user
- Template contains copies of the form's questions (text, type, order)
- No responses are copied

### Failure Postconditions

- No template is created
- The original form remains unchanged

## Business Rules

### BR-024: Templates Are Private

Templates are private to the owner

### BR-025: Any Status Allowed

Forms in any status (DRAFT, PUBLIC, CLOSED) can be saved as a template

### BR-026: Only Questions Copied

Only questions are copied to the template, not responses
