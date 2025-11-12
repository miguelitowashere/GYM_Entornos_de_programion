"""Database models for the gym backend running on MongoDB."""

from django.contrib.auth.base_user import AbstractBaseUser, BaseUserManager
from django.contrib.auth.models import PermissionsMixin
from django.core.validators import MinValueValidator
from django.db import models


class UserManager(BaseUserManager):
    """Custom user manager that uses the email as the username field."""

    def _create_user(self, email, password, **extra_fields):
        if not email:
            raise ValueError('El email es obligatorio')
        email = self.normalize_email(email)
        user = self.model(email=email, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_user(self, email, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', False)
        extra_fields.setdefault('is_superuser', False)
        return self._create_user(email, password, **extra_fields)

    def create_superuser(self, email, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('role', User.Role.ADMINISTRADOR)

        if extra_fields.get('is_staff') is not True:
            raise ValueError('El superusuario debe tener is_staff=True')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('El superusuario debe tener is_superuser=True')

        return self._create_user(email, password, **extra_fields)


class User(AbstractBaseUser, PermissionsMixin):
    """User model with roles for the gym application."""

    class Role(models.TextChoices):
        ADMINISTRADOR = 'Administrador', 'Administrador'
        ENTRENADOR = 'Entrenador', 'Entrenador'
        CLIENTE = 'Cliente', 'Cliente'

    email = models.EmailField(unique=True)
    first_name = models.CharField(max_length=150)
    last_name = models.CharField(max_length=150)
    phone = models.CharField(max_length=20, blank=True)
    role = models.CharField(max_length=20, choices=Role.choices, default=Role.CLIENTE)

    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    date_joined = models.DateTimeField(auto_now_add=True)

    objects = UserManager()

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['first_name', 'last_name']

    def __str__(self):
        return f"{self.email} ({self.role})"

    @property
    def nombre(self):
        return self.first_name

    @property
    def apellido(self):
        return self.last_name


class Membership(models.Model):
    """Gym membership plans."""

    name = models.CharField(max_length=150)
    description = models.TextField(blank=True)
    price = models.DecimalField(max_digits=10, decimal_places=2, validators=[MinValueValidator(0)])
    duration_days = models.PositiveIntegerField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.name


class ClientMembership(models.Model):
    """Assignment of a membership to a specific user."""

    class Status(models.TextChoices):
        ACTIVA = 'Activa', 'Activa'
        VENCIDA = 'Vencida', 'Vencida'
        PENDIENTE = 'Pendiente', 'Pendiente'

    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='memberships')
    membership = models.ForeignKey(Membership, on_delete=models.CASCADE, related_name='assignments')
    start_date = models.DateField()
    end_date = models.DateField()
    status = models.CharField(max_length=20, choices=Status.choices, default=Status.PENDIENTE)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.email} - {self.membership.name}"


class Payment(models.Model):
    """Payments made by clients for their memberships."""

    class Method(models.TextChoices):
        EFECTIVO = 'Efectivo', 'Efectivo'
        TARJETA = 'Tarjeta', 'Tarjeta'
        TRANSFERENCIA = 'Transferencia', 'Transferencia'

    client_membership = models.ForeignKey(ClientMembership, on_delete=models.CASCADE, related_name='payments')
    payment_date = models.DateField()
    amount = models.DecimalField(max_digits=10, decimal_places=2, validators=[MinValueValidator(0)])
    payment_method = models.CharField(max_length=20, choices=Method.choices)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Pago {self.amount} - {self.payment_method}"
