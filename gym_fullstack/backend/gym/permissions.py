"""Custom permissions for the gym API."""

from rest_framework.permissions import BasePermission


class IsAdminRole(BasePermission):
    """Allow access only to users with the Administrator role."""

    def has_permission(self, request, view):
        return bool(request.user and request.user.is_authenticated and request.user.role == 'Administrador')
