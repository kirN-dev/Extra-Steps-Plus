# Формат рецептов обработки

Все рецепты `extra_steps:processing` используют `ingredients` с явным `count` и объект `result` с `item` и `count`.
Для mixing и scraping число действий задаётся `interaction_count`. В drying длительность остаётся в `time` (тики).
Старые `ingredient`, `ingredientAmount`, `resultAmount`, `stir_count` поддерживаются для совместимости; `interaction_count` имеет приоритет над `stir_count`.
Для прессования количество расходуемого входа берётся из первого ingredients[].count, если старое ingredientAmount не задано.

Альтернативы для одного ингредиента записываются так (это один вход, а не несколько обязательных):

```json
"ingredients": [
  {
    "ingredient": [
      { "item": "extra_steps:raw_wool" },
      { "item": "extra_steps:wet_wool" }
    ],
    "count": 1
  }
]
```

Миграция сохраняет существующие результаты, количества, время и вероятности. Производственная цепочка pulp добавлена отдельно; правила выдачи на конкретных ударах описаны в SCRAPING.md.
