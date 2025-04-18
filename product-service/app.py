from fastapi import FastAPI, Request
from typing import List
from pydantic import BaseModel
import logging
import sys
from pythonjsonlogger import jsonlogger  # ✅ fixed import casing

# ────── Logging Setup ──────
logger = logging.getLogger()
logger.setLevel(logging.INFO)

log_handler = logging.StreamHandler(sys.stdout)
formatter = jsonlogger.JsonFormatter(
    '%(asctime)s %(levelname)s %(name)s %(message)s %(request_id)s %(service)s'
)
log_handler.setFormatter(formatter)
logger.handlers = [log_handler]

# ────── FastAPI App ──────
app = FastAPI(title="Product Service")

# ────── Middleware to Inject request-id & service into Logger Context ──────
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request as StarletteRequest
from logging import LoggerAdapter

class RequestIdMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: StarletteRequest, call_next):
        request_id = request.headers.get("x-request-id", "unknown")

        # Create adapter with extra context
        adapter = LoggerAdapter(logger, {
            "request_id": request_id,
            "service": "product-service"
        })

        adapter.info(f"Incoming request: {request.method} {request.url.path}")

        response = await call_next(request)
        return response

app.add_middleware(RequestIdMiddleware)

# ────── Data & Models ──────
class Product(BaseModel):
    id: int
    name: str
    price: float

# In-memory product list
products = [
    Product(id=1, name="Laptop", price=999.99),
    Product(id=2, name="Phone", price=599.99),
    Product(id=3, name="Tablet", price=399.99)
]

# ────── API Endpoints ──────

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

@app.get("/special-products")
async def get_special_products(request: Request):
    x_header = request.headers.get("x-requested-by")
    request_id = request.headers.get("x-request-id")
    return {
        "products": products,
        "requested_by": x_header,
        "request_id": request_id
    }
