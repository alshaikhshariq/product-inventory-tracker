# Improvements and Optimizations

## Summary
- Moved filtering to database queries via Spring Data JPA methods for performance.
- Added input validation for price ranges and category inputs to prevent invalid calls.
- Removed unused native query with injection risk and clarified repository API.
- Updated tests to cover validation paths and new query methods.

## Changes by Area

### Repository (ProductRepository)
- Removed unused `findProductsByCategory` native query (SQL injection risk, unused).
- Added derived queries:
  - `findByCategoryIgnoreCase(String category)`
  - `findByPriceBetween(double minPrice, double maxPrice)`
  - `findByAvailable(boolean available)`

**Benefit:** Database-level filtering reduces memory usage and is more scalable.

### Service (ProductService)
- Added validation helpers:
  - `validatePriceRange(minPrice, maxPrice)` rejects negative prices and invalid ranges.
  - `validateAndNormalizeCategory(category)` trims, rejects empty/null, lowercases for consistency.
- Refactored filters to use repository queries (no in-memory filtering):
  - `getProductsByCategory` → `findByCategoryIgnoreCase`
  - `getProductsByPriceRange` → `findByPriceBetween`
  - `getProductsByAvailability` → `findByAvailable`
- Kept DTO conversion and lowercase normalization consistent.

**Benefit:** Faster queries, safer inputs, clearer responsibilities.

### Tests (ProductServiceTest)
- Mocks updated for new repository methods.
- Added validation tests:
  - Invalid price range throws `IllegalArgumentException`.
  - Empty/whitespace/null category throws `IllegalArgumentException`.
- Adjusted boundary expectations for price range (exact matches).

**Benefit:** Validation and query-path coverage; regression safety.

## Rationale
- Performance: Avoid `findAll()` + in-memory filtering; let the DB filter.
- Security/quality: Remove unused native query with string concatenation risk.
- Robustness: Guard against invalid inputs early.
- Maintainability: Clear repository contract; simpler service logic.

## Notes / Future Enhancements
- Consider REST endpoints for external testing if needed.
- For very large batches, consider pagination or batching patterns.
- Add logging around validation failures if observability is desired.

