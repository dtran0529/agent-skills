# Example: Executable Product Activity

```text
phase-1/
├── README.md
├── foundation/
│   ├── README.md
│   └── local-runtime/
│       ├── README.md
│       ├── task-setup-container-runtime.md
│       └── task-test-runtime-smoke.md
└── catalog/
    ├── README.md
    └── product-list/
        ├── README.md
        ├── task-db-product-schema.md
        ├── task-be-product-list-api.md
        └── task-test-product-list.md
```

The product-list feature is a vertical outcome. Database, backend, and test work remain child tasks rather than separate horizontal features.

Each task states artifacts, dependencies, a target verification command, expected output, failure scenarios, acceptance criteria, and review evidence. No frontend task is created unless frontend delivery is in scope.
