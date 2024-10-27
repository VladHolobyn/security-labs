import random
import math
import sympy


def random_prime(min=100, max=1000):
    return sympy.randprime(min, max)


def generate_keys():

    p = random_prime()
    q = random_prime()
    while p == q:
        q = random_prime()


    n = p * q
    phi_n = (p - 1) * (q - 1)

    
    e = random.randint(2, phi_n)    
    while math.gcd(e, phi_n) != 1:
        e = random.randint(2, phi_n)

    d = pow(e, -1, phi_n)

    return [d, n], [e, n]


def encode(text, key):
    return pow(text, key[0], key[1])

def decode(encoded, key):
    return pow(encoded, key[0], key[1])




M = 100

private ,public = generate_keys()
encoded = encode(M, public)
decoded = decode(encoded, private)

print("Text: ", M)
print("Private key:", private, "Public key:", public)
print("Encoded: ", encoded)
print("Decoded: ", decoded)
