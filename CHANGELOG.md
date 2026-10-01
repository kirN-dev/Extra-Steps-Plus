# Changelog

All notable changes to this project are documented in this file.

Version format: `MAJOR.MINOR.PATCH`

## [1.3.0] - 2026-10-01

### Added

- Added a complete paper production chain: scraping sugar cane, mixing plant fiber, compressing pulp, drying it, and separating a paper block into sheets.
- Added the `plant_fiber`, `pulp`, `pulp_block`, and reusable eight-durability `paper_block` items.
- Added configurable scraping byproducts with a chance, trigger, and selected interaction numbers.
- Added tool requirements and configurable input costs through item consumption or durability damage.
- Added ingredient and result counts to processing recipes.
- Added tooltips for progress, durability, byproducts, and total production output.
- Added JEI integration for mixing, scraping, drying, compressing, brushing, cleaning, dyeing, and lye-water processing.
- Added JEI categories, catalysts, equipment requirements, and localized recipe explanations.
- Added scraping and drying progress display in Jade.
- Added Russian and English translations for new items, tooltips, and integration interfaces.

### Changed

- Unified the processing recipe format around `ingredients`, `result`, and the `interaction_count` field.
- Moved scraping progress from the drying rack to the processed item's NBT so it survives extraction, transfer, and rack destruction.
- Reworked mixing and compressing to respect ingredient counts and water consumption.
- Expanded recipe displays so JEI shows interaction counts, water costs, base drying time, tools, probabilities, and expected output.
- Preserved compatibility with the legacy `ingredient`, `ingredientAmount`, `resultAmount`, and `stir_count` recipe fields.

### Fixed

- Fixed incomplete scraping progress being lost when moving an item between drying racks or restarting the world.
- Fixed output generation when a reusable input breaks on the final interaction of a cycle.
- Fixed the main output being obtainable from an incomplete cycle after the input breaks early.
- Fixed input consumption varying because of Creative mode or enchantments.
- Fixed the display of input amounts, byproducts, and full-cycle output in JEI.
- Fixed Jade scraping progress for recipes with a configurable interaction count.

### Removed

- Removed scraping progress dependence on the drying rack's internal counter; legacy data is migrated to the item automatically.

---

# История изменений

Все значимые изменения проекта фиксируются в этом файле.

Формат версий: `MAJOR.MINOR.PATCH`

## [1.3.0] - 2026-10-01

### Добавлено

- Добавлена полная производственная цепочка бумаги: скобление сахарного тростника, смешивание растительного волокна, прессование бумажной массы, сушка и разделение бумажного блока на листы.
- Добавлены предметы `plant_fiber`, `pulp`, `pulp_block` и многоразовый `paper_block` с восемью единицами прочности.
- Добавлены настраиваемые побочные продукты рецептов скобления с шансом, моментом выдачи и выбором конкретных действий.
- Добавлены требования к инструментам и настраиваемая стоимость входного предмета: расход предметов или потеря прочности.
- Добавлена поддержка количества ингредиентов и результатов в рецептах обработки.
- Добавлены подсказки о прогрессе, прочности, побочных продуктах и суммарном выходе продукции.
- Добавлена интеграция JEI для смешивания, скобления, сушки, прессования, вычёсывания, очистки, окрашивания и работы со щёлочной водой.
- Добавлены категории, катализаторы, требования к оборудованию и локализованные пояснения рецептов в JEI.
- Добавлено отображение прогресса скобления и сушки в Jade.
- Добавлены русские и английские переводы для новых предметов, подсказок и интерфейсов интеграций.

### Изменено

- Унифицирован формат рецептов обработки: входы перенесены в `ingredients`, результат — в `result`, а количество действий задаётся через `interaction_count`.
- Прогресс скобления перенесён из сушильной стойки в NBT обрабатываемого предмета и теперь сохраняется при извлечении, переносе и разрушении стойки.
- Переработана логика смешивания и прессования с учётом требуемого количества ингредиентов и расхода воды.
- Расширено отображение рецептов: JEI показывает число действий, расход воды, базовое время сушки, инструменты, вероятности и ожидаемый выход.
- Сохранена совместимость со старыми полями рецептов `ingredient`, `ingredientAmount`, `resultAmount` и `stir_count`.

### Исправлено

- Исправлено сохранение незавершённого прогресса скобления при переносе предмета между сушильными стойками и перезапуске мира.
- Исправлена выдача результата при поломке многоразового входного предмета на последнем действии цикла.
- Исправлена возможность получить основной результат из незавершённого цикла после преждевременной поломки входного предмета.
- Исправлен недетерминированный расход входных предметов под влиянием творческого режима или зачарований.
- Исправлено отображение количества входов, побочных продуктов и полного выхода цикла в JEI.
- Исправлено отображение прогресса скобления в Jade для рецептов с настраиваемым количеством действий.

### Удалено

- Удалена зависимость прогресса скобления от внутреннего счётчика сушильной стойки; старые данные автоматически переносятся в предмет.
