from fastapi import FastAPI
from typing import List
from pydantic import BaseModel

app = FastAPI(title="Product Service")

class Product(BaseModel):
    id: int
    name: str
    price: float

# In-memory data
products = [
    Product(id=1, name="Laptop", price=999.99),
    Product(id=2, name="Phone", price=599.99),
    Product(id=3, name="Tablet", price=399.99)
]

@app.get("/products", response_model=List[Product])
def get_all_products():
    return products

@app.get("/products/{id}", response_model=Product)
def get_product_by_id(id: int):
    for product in products:
        if product.id == id:
            return product
    return {"error": "Product not found"}

@app.get("/products/status")
def health_check():
    return {"status": "UP", "service": "Product Service"}
