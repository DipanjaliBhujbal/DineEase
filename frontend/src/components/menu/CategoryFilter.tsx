import type { MenuCategory } from "../../types/category";

interface CategoryFilterProps {
  categories: MenuCategory[];
  selectedCategory: number | null;
  onSelect: (categoryId: number | null) => void;
}

export default function CategoryFilter({
  categories,
  selectedCategory,
  onSelect,
}: CategoryFilterProps) {
  return (
    <div className="category-filter" aria-label="Filter menu by category">
      <button
        type="button"
        className={selectedCategory === null ? "category-button active" : "category-button"}
        onClick={() => onSelect(null)}
      >
        All Items
      </button>

      {categories.map((category) => (
        <button
          key={category.id}
          type="button"
          className={
            selectedCategory === category.id
              ? "category-button active"
              : "category-button"
          }
          onClick={() => onSelect(category.id)}
        >
          {category.name}
        </button>
      ))}
    </div>
  );
}
