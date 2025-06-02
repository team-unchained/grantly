package grantly.member.application.service.exceptions

class DuplicateEmailException : RuntimeException("Member with this email already exists")
