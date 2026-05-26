"""Constantes de la aplicación"""

# Roles de usuario
ROLE_BUYER = 'buyer'
ROLE_VENDOR = 'vendor'
ROLE_ADMIN = 'admin'
VALID_ROLES = [ROLE_BUYER, ROLE_VENDOR, ROLE_ADMIN]

# Estados de transacción
TX_STATUS_PENDING = 'pending'
TX_STATUS_CONFIRMED = 'confirmed'
TX_STATUS_COMPLETED = 'completed'
TX_STATUS_CANCELLED = 'cancelled'
TX_STATUS_DISPUTED = 'disputed'
VALID_TX_STATUSES = [TX_STATUS_PENDING, TX_STATUS_CONFIRMED, TX_STATUS_COMPLETED, 
                      TX_STATUS_CANCELLED, TX_STATUS_DISPUTED]

# Estados de oferta
OFFER_STATUS_ACTIVE = 'active'
OFFER_STATUS_PAUSED = 'paused'
OFFER_STATUS_CLOSED = 'closed'
VALID_OFFER_STATUSES = [OFFER_STATUS_ACTIVE, OFFER_STATUS_PAUSED, OFFER_STATUS_CLOSED]

# Tipos de oferta
OFFER_TYPE_BUY = 'buy'
OFFER_TYPE_SELL = 'sell'
VALID_OFFER_TYPES = [OFFER_TYPE_BUY, OFFER_TYPE_SELL]

# Monedas soportadas
CURRENCIES = {
    'BTC': {'name': 'Bitcoin', 'symbol': '₿'},
    'ETH': {'name': 'Ethereum', 'symbol': 'Ξ'},
    'USDT': {'name': 'Tether', 'symbol': '$'},
    'PEN': {'name': 'Peruvian Sol', 'symbol': 'S/'},
    'USD': {'name': 'US Dollar', 'symbol': '$'},
}
