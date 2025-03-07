package grantly.user.application.service.exceptions

class DuplicateEmailException : RuntimeException("User with this email already exists")
