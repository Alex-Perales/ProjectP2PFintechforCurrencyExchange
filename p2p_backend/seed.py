#!/usr/bin/env python3
"""
Seed script — currencies, exchange rates, and 3 test users.
Runs on container startup (development).
"""
import time
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))


def seed():
    from app import create_app
    from app.core.database import db
    from app.models import Currency, ExchangeRate
    from app.models.user import User

    app = create_app('development')
    with app.app_context():

        # ── Currencies ──────────────────────────────────────
        for code, name, symbol in [
            ('PEN', 'Sol Peruano', 'S/'),
            ('USD', 'Dólar Americano', '$'),
            ('EUR', 'Euro', '€'),
            ('BRL', 'Real Brasileño', 'R$'),
        ]:
            if not Currency.query.filter_by(code=code).first():
                db.session.add(Currency(code=code, name=name, symbol=symbol))
                print(f'  + Currency {code}')

        # ── Exchange rates ───────────────────────────────────
        for frm, to, rate in [
            ('USD', 'PEN', 3.72),
            ('PEN', 'USD', 0.2688),
            ('EUR', 'PEN', 4.05),
            ('PEN', 'EUR', 0.2469),
            ('USD', 'EUR', 0.9220),
            ('EUR', 'USD', 1.0845),
        ]:
            if not ExchangeRate.query.filter_by(from_currency=frm, to_currency=to).first():
                db.session.add(ExchangeRate(from_currency=frm, to_currency=to, rate=rate))
                print(f'  + Rate {frm}->{to}: {rate}')

        # ── Test users ───────────────────────────────────────
        for ud in [
            {'email': 'comprador@peruexchange.com', 'password': 'Comprador123!',
             'full_name': 'Carlos Comprador', 'role': 'buyer'},
            {'email': 'vendedor@peruexchange.com', 'password': 'Vendedor123!',
             'full_name': 'Victor Vendedor', 'role': 'vendor'},
            {'email': 'admin@peruexchange.com', 'password': 'Admin123!',
             'full_name': 'Ana Admin', 'role': 'admin'},
        ]:
            if not User.query.filter_by(email=ud['email']).first():
                u = User(
                    email=ud['email'],
                    full_name=ud['full_name'],
                    role=ud['role'],
                    kyc_verified=True,
                    rating=4.8,
                    total_transactions=12,
                )
                u.set_password(ud['password'])
                db.session.add(u)
                print(f"  + User {ud['email']} [{ud['role']}]")

        db.session.commit()

        # ── Test Offers ──────────────────────────────────────
        vendor = User.query.filter_by(email='vendedor@peruexchange.com').first()
        if vendor:
            from app.models import Offer
            import json
            if not Offer.query.filter_by(vendor_id=vendor.id).first():
                # 1. Partial Offer
                db.session.add(Offer(
                    vendor_id=vendor.id,
                    from_currency='USD',
                    to_currency='PEN',
                    amount=1200.0,
                    available_amount=1200.0,
                    price_per_unit=3.780,
                    offer_type='partial',
                    status='active',
                    min_transaction=50.0,
                    max_transaction=600.0,
                    payment_methods=json.dumps(['BCP', 'Yape'])
                ))
                # 2. Full Offer
                db.session.add(Offer(
                    vendor_id=vendor.id,
                    from_currency='USD',
                    to_currency='PEN',
                    amount=800.0,
                    available_amount=800.0,
                    price_per_unit=3.750,
                    offer_type='full',
                    status='active',
                    min_transaction=800.0,
                    max_transaction=800.0,
                    payment_methods=json.dumps(['BCP'])
                ))
                print('  + Seeded 2 active offers for Victor Vendedor')

        db.session.commit()
        print('Seed completed.')


if __name__ == '__main__':
    time.sleep(2)
    seed()
