"""Serializers for the gym API."""

from django.contrib.auth import get_user_model
from rest_framework import serializers

from .models import ClientMembership, Membership, Payment

User = get_user_model()


class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, required=False)

    class Meta:
        model = User
        fields = (
            'id',
            'email',
            'first_name',
            'last_name',
            'phone',
            'role',
            'password',
        )
        extra_kwargs = {
            'id': {'read_only': True},
        }

    def to_internal_value(self, data):
        if 'nombre' in data or 'apellido' in data:
            data = data.copy()
            if 'nombre' in data:
                data['first_name'] = data.pop('nombre')
            if 'apellido' in data:
                data['last_name'] = data.pop('apellido')
        return super().to_internal_value(data)

    def create(self, validated_data):
        password = validated_data.pop('password')
        user = User(**validated_data)
        user.set_password(password)
        user.save()
        return user

    def update(self, instance, validated_data):
        password = validated_data.pop('password', None)
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        if password:
            instance.set_password(password)
        instance.save()
        return instance

    def to_representation(self, instance):
        data = super().to_representation(instance)
        data['nombre'] = instance.first_name
        data['apellido'] = instance.last_name
        data.pop('first_name', None)
        data.pop('last_name', None)
        return data


class MembershipSerializer(serializers.ModelSerializer):
    class Meta:
        model = Membership
        fields = ('id', 'name', 'description', 'price', 'duration_days', 'created_at')
        read_only_fields = ('id', 'created_at')

    def to_representation(self, instance):
        data = super().to_representation(instance)
        data['nombre'] = instance.name
        data['descripcion'] = instance.description
        data['precio'] = float(instance.price)
        data['duracionDias'] = instance.duration_days
        data.pop('name', None)
        data.pop('description', None)
        data.pop('duration_days', None)
        return data

    def to_internal_value(self, data):
        if 'nombre' in data:
            data = data.copy()
            data['name'] = data.pop('nombre')
            data['description'] = data.pop('descripcion', '')
            data['price'] = data.get('precio')
            data['duration_days'] = data.get('duracionDias')
        return super().to_internal_value(data)


class SimpleUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'first_name', 'last_name', 'email', 'role')

    def to_representation(self, instance):
        data = super().to_representation(instance)
        data['nombre'] = instance.first_name
        data['apellido'] = instance.last_name
        data.pop('first_name', None)
        data.pop('last_name', None)
        return data


class ClientMembershipSerializer(serializers.ModelSerializer):
    usuario = SimpleUserSerializer(source='user', read_only=True)
    membresia = MembershipSerializer(source='membership', read_only=True)
    usuario_id = serializers.PrimaryKeyRelatedField(
        source='user', queryset=User.objects.all(), write_only=True
    )
    membresia_id = serializers.PrimaryKeyRelatedField(
        source='membership', queryset=Membership.objects.all(), write_only=True
    )
    fechaInicio = serializers.DateField(source='start_date')
    fechaFin = serializers.DateField(source='end_date')
    estado = serializers.CharField(source='status')

    class Meta:
        model = ClientMembership
        fields = (
            'id',
            'usuario',
            'membresia',
            'usuario_id',
            'membresia_id',
            'fechaInicio',
            'fechaFin',
            'estado',
            'created_at',
        )
        read_only_fields = ('id', 'created_at', 'usuario', 'membresia')

    def to_representation(self, instance):
        data = super().to_representation(instance)
        data.pop('usuario_id', None)
        data.pop('membresia_id', None)
        return data

    def to_internal_value(self, data):
        mutable_data = data.copy()
        if isinstance(mutable_data.get('usuario'), dict):
            mutable_data['usuario_id'] = mutable_data['usuario'].get('id')
        if isinstance(mutable_data.get('membresia'), dict):
            mutable_data['membresia_id'] = mutable_data['membresia'].get('id')
        return super().to_internal_value(mutable_data)


class PaymentSerializer(serializers.ModelSerializer):
    clienteMembresiaId = serializers.PrimaryKeyRelatedField(
        source='client_membership', queryset=ClientMembership.objects.all(), write_only=True
    )
    fechaPago = serializers.DateField(source='payment_date')
    monto = serializers.DecimalField(source='amount', max_digits=10, decimal_places=2)
    metodoPago = serializers.CharField(source='payment_method')
    clienteNombre = serializers.SerializerMethodField()
    membresiaNombre = serializers.SerializerMethodField()

    class Meta:
        model = Payment
        fields = (
            'id',
            'clienteMembresiaId',
            'fechaPago',
            'monto',
            'metodoPago',
            'clienteNombre',
            'membresiaNombre',
            'created_at',
        )
        read_only_fields = ('id', 'clienteNombre', 'membresiaNombre', 'created_at')

    def get_clienteNombre(self, obj):
        usuario = obj.client_membership.user
        return f"{usuario.first_name} {usuario.last_name}" if usuario else None

    def get_membresiaNombre(self, obj):
        membresia = obj.client_membership.membership
        return membresia.name if membresia else None

    def to_representation(self, instance):
        data = super().to_representation(instance)
        data['monto'] = float(data['monto'])
        return data
