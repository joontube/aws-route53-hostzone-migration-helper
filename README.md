# Amazon Route 53 Hostzone Migration Helper

## Overview
The Hostzone Migration Helper is a tool designed to simplify and automate DNS record transformations, making it easier to work with complex DNS zone files.

---

## Prerequisites

Before using the Hostzone Migration Helper, ensure you have the following installed on your machine:

- Java 17 or higher
- Gradle 7.5 or higher
- Docker (if running in a containerized environment)

---

## Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-repo/hostzone-migration-helper.git
   cd hostzone-migration-helper
   ```

2. **Build the project**:
   ```bash
   ./gradlew clean build
   ```

3. **Run the application**:
   ```bash
   java -jar build/libs/hostzone-migration-helper.jar
   ```

---

## Usage

### Input File Example
The input JSON file should contain a list of `ResourceRecordSets` like the following:

```json
{
  "ResourceRecordSets": [
    {
      "Name": "example.com.",
      "Type": "NS",
      "TTL": 172800,
      "ResourceRecords": [
        { "Value": "ns-1.example.com." },
        { "Value": "ns-2.example.com." },
        { "Value": "ns-3.example.com." },
        { "Value": "ns-4.example.com." }
      ]
    },
    {
      "Name": "example.com.",
      "Type": "SOA",
      "TTL": 900,
      "ResourceRecords": [
        { "Value": "ns-1.example.com. admin.example.com. 2023123001 7200 3600 1209600 3600" }
      ]
    },
    {
      "Name": "example.com.",
      "Type": "A",
      "TTL": 300,
      "ResourceRecords": [
        { "Value": "192.0.2.1" }
      ]
    },
    {
      "Name": "example.com.",
      "Type": "MX",
      "TTL": 300,
      "ResourceRecords": [
        { "Value": "10 mail.example.com." }
      ]
    }
}
```

### Transformed Output Example
The application will process the input and produce the following transformed structure:

```json
{
  "Changes": [
    {
      "Action": "CREATE",
      "ResourceRecordSet": {
        "Name": "example.com.",
        "Type": "A",
        "TTL": 300,
        "ResourceRecords": [
          { "Value": "192.0.2.1" }
        ]
  ]
      }
    },
    {
      "Action": "CREATE",
      "ResourceRecordSet": {
        "Name": "example.com.",
        "Type": "MX",
        "TTL": 300,
        "ResourceRecords": [
          { "Value": "10 mail.example.com." }
        ]
      }
    }
  ]
}
```

---

## Testing

To run the test suite, execute:

```bash
./gradlew test
```

---

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

