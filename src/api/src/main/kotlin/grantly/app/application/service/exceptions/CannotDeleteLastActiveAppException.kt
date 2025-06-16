package grantly.app.application.service.exceptions

class CannotDeleteLastActiveAppException : RuntimeException("Members should have at least one active app")
