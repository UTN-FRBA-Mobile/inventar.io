import datetime
import os
import barcode
from barcode.writer import ImageWriter


# Function to get valid 12-digit numeric input
def get_numeric_input():
    while True:
        data = input("Enter a 12-digit numeric code: ")
        if data.isdigit() and len(data) == 12:
            return data
        else:
            print("Invalid input. Please enter exactly 12 digits.")


try:
    # Create 'output' directory if it doesn't exist
    output_dir = os.path.join("output", "barcode")
    os.makedirs(output_dir, exist_ok=True)

    # Get numeric data from the user
    numeric_code = get_numeric_input()

    # Generate timestamped filename
    timestamp = datetime.datetime.now().strftime("%Y.%m.%d-%H.%M.%S")
    filename = f"barcode_{timestamp}"

    # Generate EAN-13 barcode
    barcode_class = barcode.get_barcode_class('ean13')
    barcode_obj = barcode_class(numeric_code, writer=ImageWriter())

    # Save barcode image
    output_path = os.path.join(output_dir, filename)
    barcode_obj.save(output_path)

    print(f"EAN-13 Barcode saved at '{output_path}.png'")

# Catch any exception, print it
except Exception as e:
    print(f"An error occurred: {e}")
