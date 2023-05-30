from dataclasses import dataclass
import random

#STANDARDS FOR EFFICIENT CRYPTOGRAPHY
#SEC 2: Recommended Elliptic Curve Domain Parameters
#https://www.secg.org/SEC2-Ver-1.0.pdf
Parameters = {
    "secp160k1": {
        "a" : 0,
        "b" : 7,
        "p" : 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFAC73,
        "gx" : 0x3B4C382CE37AA192A4019E763036F4F5DD4D7EBB,
        "gy" : 0x938CF935318FDCED6BC28286531733C3F03C4FEE,
        "n" : 0x0100000000000000000001b8fa16dfab9aca16b6b3,
    },
    "secp192k1": {
        "a" : 0,
        "b" : 3,
        "p" : 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37,
        "gx" : 0xDB4FF10EC057E9AE26B07D0280B7F4341DA5D1B1EAE06C7D,
        "gy" : 0x9B2F2F6D9C5628A7844163D015BE86344082AA88D95E2F9D,
        "n" : 0xFFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D,  
    },
    "secp224k1": {
        "a" : 0,
        "b" : 5,
        "p" : 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFE56D,
        "gx" : 0xA1455B334DF099DF30FC28A169A467E9E47075A90F7E650EB6B7A45C,
        "gy" : 0x7E089FED7FBA344282CAFBD6F7E319F7C0B0BD59E2CA4BDB556D61A5,
        "n" : 0x10000000000000000000000000001DCE8D2EC6184CAF0A971769FB1F7,
    },
    "secp256k1": {
        "a" : 0,
        "b" : 7,
        "p" : 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F,
        "gx" : 0x79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798,
        "gy" : 0x483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8,
        "n" : 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141,
    },
    "test": {
        "a" : 0,
        "b" : 7,
        "p" : 17,
        "gx" : 15,
        "gy" : 13,
        "n" : 18
    }
}

#Class Structure reference from
#https://onyb.gitbook.io/secp256k1-python/
@dataclass
class FiniteField:
    p: int

    def __contains__(self, coordinate: "FieldCoordinate") -> bool:
        return 0 <= coordinate.value < self.p

@dataclass
class FieldCoordinate:
    value: int
    field: FiniteField

    def __repr__(self):
        return f"{self.value}"

    def __add__(self, other: "FieldCoordinate") -> "FieldCoordinate":
        return FieldCoordinate (
            value = (self.value + other.value) % self.field.p,
            field = self.field
        )

    def __sub__(self, other: "FieldCoordinate") -> "FieldCoordinate":
        return FieldCoordinate (
            value = (self.value - other.value) % self.field.p,
            field = self.field
        )
    
    def __rmul__(self, scalar: int) -> "FieldCoordinate":
        return FieldCoordinate(
            value = (self.value * scalar) % self.field.p,
            field = self.field
        )

    def __mul__(self, other: "FieldCoordinate") -> "FieldCoordinate":
        return FieldCoordinate (
            value = (self.value * other.value) % self.field.p,
            field = self.field
        )

    def __pow__(self, exponent: int) -> "FieldCoordinate":
        return FieldCoordinate (
            value = pow(self.value, exponent, self.field.p),
            field = self.field
        )

    def __truediv__(self, other: "FieldCoordinate") -> "FieldCoordinate":
        other_inv = other ** -1
        return self * other_inv

@dataclass
class EllipticCurve:
    a: int
    b: int
    field: FiniteField

    def __contains__(self, point: "Point") -> bool:
        return point.y ** 2 == point.x ** 3 + self.a * point.x + self.b

    def __post_init__(self):
        # Encapsulate int parameters in FieldCoordinate
        self.a = FieldCoordinate(self.a, self.field)
        self.b = FieldCoordinate(self.b, self.field)

        # Check a < p and b < p
        if self.a not in self.field or self.b not in self.field:
            raise ValueError

@dataclass
class Point:
    x: int
    y: int
    n: int
    curve: EllipticCurve

    def __repr__(self):
        return f"({self.x}, {self.y})"

    def __post_init__(self):
        # Check if Point is O
        if self.x is None and self.y is None:
            return

        # Encapsulate int parameters in Coordinate
        self.x = FieldCoordinate(self.x, self.curve.field)
        self.y = FieldCoordinate(self.y, self.curve.field)

        # Verify if the point satisfies the curve equation
        if self not in self.curve:
            raise ValueError

    def __add__(self, other):
        O = Point(x=None, y=None, n=self.n, curve=self.curve)
        
        # (Identity) P + O = O + P
        if self == O:
            return other
        if other == O:
            return self

        # (Inverse) P + (-P) = (-P) + P = O
        if self.x == other.x and self.y == (-1 * other.y):
            return O

        # Addition P ≠ Q
        if self.x != other.x:
            x1, x2 = self.x, other.x
            y1, y2 = self.y, other.y

            λ = (y2 - y1) / (x2 - x1)
            x3 = λ ** 2 - x1 - x2
            y3 = λ * (x1 - x3) - y1

            return Point(
                x = x3.value,
                y = y3.value,
                n = self.n,
                curve = self.curve
            )

        # (Vertical Tangent) Addition P = Q
        if self == other and self.y == float("inf"):
            return O

        # Addition P = Q
        if self == other:
            x1, y1, a = self.x, self.y, self.curve.a

            λ = (3 * x1 ** 2 + a) / (2 * y1)
            x3 = λ ** 2 - 2 * x1
            y3 = λ * (x1 - x3) - y1

            return Point(
                x = x3.value,
                y = y3.value,
                n = self.n,
                curve = self.curve
            )

    def __rmul__(self, scalar: int) -> "Point":
        nG = Point(x=None, y=None, n=self.n, curve=self.curve)
        for _ in range(scalar):
            nG = nG + self
        return nG

class ElGammelECC:
    def __init__(self):
        self.setting: str
        self.field: FiniteField
        self.curve: EllipticCurve
        self.g: Point
        
        self.PrivateKey: FieldCoordinate
        self.PublicKey: Point

    def setStandardCurve(self, setting: str):
        if setting not in Parameters.keys():
            raise ValueError("Invalid Curve Setting")

        self.setting = setting
      
        self.field = FiniteField(
            p = Parameters[setting]["p"])
        
        self.curve = EllipticCurve(
            a = Parameters[setting]["a"],
            b = Parameters[setting]["b"],
            field = self.field)

        self.g = Point(
            x = Parameters[setting]["gx"],
            y = Parameters[setting]["gy"],
            n = Parameters[setting]["n"],
            curve = self.curve)

    #https://onlinelibrary.wiley.com/doi/epdf/10.1002/sec.1702
    #6.1. Mapping algorithm
    def messageToPoint(self, plaintext: str) -> Point:
        encode = ''
        return

    def keyGen(self, PrivateKey=None):
        if PrivateKey != None:
            self.PrivateKey = FieldCoordinate(PrivateKey, self.g.n)
        else:
            randInt = random.randint(1, self.g.n - 1)
            self.PrivateKey = FieldCoordinate(randInt, self.g.n)

        self.PublicKey = self.PrivateKey.value * self.g

    def encrypt(self, Plaintext, PublicKey):
        return

    def decrypt(self, Ciphertext, PrivateKey):
        return

if __name__ == "__main__":
    test = ElGammelECC()

    # Setting: 1
    # Choose an elliptic curve from four different standardized elliptic curves (secp160/192/224/256).
    test.setStandardCurve("test")
    #test.setStandardCurve("secp256k1")

    # Setting: 2 (Optional)
    # Select the generator, private key, and random integers as you want.
    #crypto.selectGenerator()
    #crypto.setPrivateKey()
    #crypto.setRandomIntegers()

    print(f"Selected Curve: {test.setting}")

    print("\nCurve Parameters: ")
    print(f"a = {test.curve.a}")
    print(f"b = {test.curve.b}")
    print(f"p = {test.field.p}")

    print("\nGenerator: ")
    print(f"g = {test.g}")
    print(f"n = {test.g.n}")

    print("\nn*Generators:")
    for n in range(0, 18):
        print(f"{n} * G = {n * test.g}")

    test.keyGen(PrivateKey=13)

    print(f"\nPrivate Key: {test.PrivateKey}")
    print(f"Public Key: {test.PublicKey}\n")

    test.messageToPoint("Alex Perrin")