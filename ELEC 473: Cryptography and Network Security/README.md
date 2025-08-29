# Elliptic Curve ElGamal Encryption Implementation

A Python implementation of ElGamal encryption using elliptic curves, demonstrating advanced cryptographic concepts and mathematical programming techniques.

## Overview

This project implements the ElGamal encryption scheme adapted for elliptic curves, providing a foundation for secure public-key cryptography. The implementation includes support for multiple standardized elliptic curves (secp160k1, secp192k1, secp224k1, secp256k1) and demonstrates the mathematical foundations of modern cryptographic systems.

## Key Features

- **Complete Elliptic Curve Implementation**: Custom classes for finite fields, field coordinates, elliptic curves, and points
- **Standard Curve Support**: Implementation of SEC 2 recommended elliptic curve domain parameters
- **Modular Design**: Object-oriented architecture with clean separation of concerns
- **Mathematical Operations**: Full implementation of elliptic curve point arithmetic and field operations
- **Cryptographic Key Generation**: Secure public/private key pair generation

## Technical Skills Demonstrated

### Mathematical Programming
- **Abstract Algebra**: Implementation of finite field arithmetic and elliptic curve group operations
- **Number Theory**: Modular arithmetic, multiplicative inverses, and quadratic residues
- **Cryptographic Mathematics**: Understanding of discrete logarithm problems and elliptic curve cryptography

### Software Engineering
- **Object-Oriented Design**: Clean class hierarchy with proper encapsulation and abstraction
- **Python Best Practices**: Use of dataclasses, type hints, and pythonic idioms
- **Mathematical Libraries**: Integration with external libraries for advanced number theory operations

### Cryptography Expertise
- **Public Key Cryptography**: Understanding of asymmetric encryption principles
- **Elliptic Curve Cryptography**: Implementation of ECC operations and security parameters
- **Standard Compliance**: Implementation follows SEC 2 recommendations for curve parameters

### Algorithm Implementation
- **Point Addition**: Complex conditional logic for handling different cases in elliptic curve point addition
- **Scalar Multiplication**: Efficient computation of point multiplication
- **Modular Arithmetic**: Custom implementation of field operations with proper modular reduction

## Files

- `ElGammelECC.py`: Main implementation containing all elliptic curve classes and ElGamal encryption framework
- `modsqrt.py`: Tonelli-Shanks algorithm implementation for computing modular square roots

## Mathematical Foundation

The implementation is based on elliptic curves of the form:
```
y² = x³ + ax + b (mod p)
```

Supporting operations include:
- Point addition and doubling on elliptic curves
- Scalar multiplication of points
- Finite field arithmetic operations
- Key generation using secure random number generation

## Supported Curves

The implementation includes parameters for several standardized elliptic curves:
- **secp160k1**: 160-bit curve with Koblitz form
- **secp192k1**: 192-bit curve with Koblitz form  
- **secp224k1**: 224-bit curve with Koblitz form
- **secp256k1**: 256-bit curve (used in Bitcoin)

## Usage Example

```python
# Initialize the system
ecc = ElGammelECC()

# Set up a standard curve
ecc.setStandardCurve("secp256k1")

# Generate key pair
ecc.keyGen()

# Display curve parameters and keys
print(f"Curve: y² = x³ + {ecc.curve.a}x + {ecc.curve.b} (mod {ecc.field.p})")
print(f"Private Key: {ecc.PrivateKey}")
print(f"Public Key: {ecc.PublicKey}")
```

## Security Considerations

This implementation is designed for educational and demonstration purposes. The security of the system depends on:
- The discrete logarithm problem on elliptic curves
- Proper random number generation for key creation
- Secure parameter selection for the chosen elliptic curves

## Technical Requirements

- Python 3.7+
- Basic understanding of elliptic curve cryptography
- Mathematical background in finite fields and group theory