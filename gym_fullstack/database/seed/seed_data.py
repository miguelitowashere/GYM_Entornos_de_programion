"""Script de datos iniciales para el proyecto del gimnasio."""

from datetime import date, timedelta

from django.contrib.auth import get_user_model

from gym.models import ClientMembership, Membership, Payment


User = get_user_model()


ADMIN_EMAIL = "admin@gym.com"
COACH_EMAIL = "coach@gym.com"
CLIENT_EMAIL = "cliente@gym.com"


def get_or_create_user(email, password, first_name, last_name, role, phone=""):
    usuario, creado = User.objects.get_or_create(
        email=email,
        defaults={
            "first_name": first_name,
            "last_name": last_name,
            "role": role,
            "phone": phone,
        },
    )
    if creado:
        usuario.set_password(password)
        # Mantener consistencia con flags por rol
        if role == User.Role.ADMINISTRADOR:
            usuario.is_staff = True
            usuario.is_superuser = True
        elif role == User.Role.ENTRENADOR:
            usuario.is_staff = True
        usuario.save()
        print(f"Creado usuario {email} ({role})")
    else:
        print(f"Usuario {email} ya existía, se mantuvo la información actual")
    return usuario


def create_memberships():
    plans = [
        {
            "name": "Mensual",
            "description": "Acceso ilimitado por 30 días",
            "price": 120000,
            "duration_days": 30,
        },
        {
            "name": "Trimestral",
            "description": "Ideal para nuevos clientes con seguimiento personalizado",
            "price": 330000,
            "duration_days": 90,
        },
        {
            "name": "Anual",
            "description": "Plan completo con asesoría nutricional incluida",
            "price": 1200000,
            "duration_days": 365,
        },
    ]

    memberships = []
    for data in plans:
        membership, _ = Membership.objects.get_or_create(name=data["name"], defaults=data)
        memberships.append(membership)
    print(f"Hay {Membership.objects.count()} membresías registradas")
    return memberships


def assign_memberships(client, memberships):
    assignments = []
    today = date.today()

    for membership in memberships[:2]:
        start = today - timedelta(days=15)
        end = start + timedelta(days=membership.duration_days)
        assignment, created = ClientMembership.objects.get_or_create(
            user=client,
            membership=membership,
            defaults={
                "start_date": start,
                "end_date": end,
                "status": ClientMembership.Status.ACTIVA,
            },
        )
        if created:
            print(f"Asignada membresía {membership.name} a {client.email}")
        assignments.append(assignment)

    return assignments


def create_payments(assignments):
    for assignment in assignments:
        payment, created = Payment.objects.get_or_create(
            client_membership=assignment,
            payment_date=assignment.start_date + timedelta(days=1),
            defaults={
                "amount": assignment.membership.price,
                "payment_method": Payment.Method.TRANSFERENCIA,
            },
        )
        if created:
            print(
                "Registrado pago de",
                f"{payment.amount} para {assignment.membership.name} de {assignment.client_membership.user.email}",
            )


if __name__ == "__main__":
    admin = get_or_create_user(
        ADMIN_EMAIL,
        "Admin123!",
        "Admin",
        "Principal",
        User.Role.ADMINISTRADOR,
        phone="3001234567",
    )
    coach = get_or_create_user(
        COACH_EMAIL,
        "Coach123!",
        "Carla",
        "Entrenadora",
        User.Role.ENTRENADOR,
        phone="3009876543",
    )
    client = get_or_create_user(
        CLIENT_EMAIL,
        "Cliente123!",
        "Carlos",
        "Cliente",
        User.Role.CLIENTE,
        phone="3007654321",
    )

    memberships = create_memberships()
    assignments = assign_memberships(client, memberships)
    create_payments(assignments)

    print("\nDatos de ejemplo listos. Puedes iniciar sesión en el frontend con:")
    print("  • admin@gym.com / Admin123! (Administrador)")
    print("  • coach@gym.com / Coach123! (Entrenador)")
    print("  • cliente@gym.com / Cliente123! (Cliente)")
