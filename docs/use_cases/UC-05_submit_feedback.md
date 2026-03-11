# Use Case: Submit Feedback

## Overview

**Use Case ID:** UC-05
**Use Case Name:** Submit Feedback
**Primary Actor:** Anonymous User
**Goal:** Submit feedback via a public form
**Status:** Implemented

## Preconditions

- User has the form link or QR code
- Form exists and is in PUBLIC status

## Main Success Scenario

1. User opens `/form/{publicToken}` (via link or QR code)
2. System loads the form using the token
3. System checks for a submission cookie for this form
4. System displays the form title, speaker, date, and location
5. For each question:
   - RATING: RadioButtonGroup with options 1-5 (localized labels)
   - TEXT: TextArea with placeholder
6. User fills out the form
7. User clicks "Submit"
8. System creates FeedbackResponse with FeedbackAnswers
9. System sets a submission cookie for this form
10. System displays a thank-you page

## Alternative Flows

### A1: Form Not Found

**Trigger:** publicToken does not exist in the database
**Flow:**

1. System displays "Form not found" message

### A2: Form Not Public

**Trigger:** Form is not in PUBLIC status
**Flow:**

1. System displays "Form not available" message

### A3: Already Submitted

**Trigger:** Submission cookie detected for this form
**Flow:**

1. System displays "Already submitted" message with a thank-you note

## Postconditions

### Success Postconditions

- New FeedbackResponse with FeedbackAnswers saved
- Submission cookie set in the browser
- Thank-you page is displayed

### Failure Postconditions

- No FeedbackResponse is created
- Error message is displayed

## Business Rules

### BR-007: Rating Scale

Ratings are 1-5 (Very poor to Very good)

### BR-008: Optional Text Answers

Text answers are optional (only non-empty ones are saved)

### BR-009: New Response Per Submission

Each submission creates a new FeedbackResponse. Duplicate submissions are prevented via cookies (BR-011).

### BR-010: No Authentication

No authentication required for submitting feedback

### BR-011: Duplicate Submission Prevention

After submitting feedback, a browser cookie (`feedback_submitted_{formId}`) is set. On subsequent visits to the same form, the system detects the cookie and shows an "already submitted" message. This is a soft prevention mechanism — clearing cookies allows resubmission.
