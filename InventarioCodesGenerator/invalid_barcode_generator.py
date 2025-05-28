import os
import datetime
import barcode
from barcode.writer import ImageWriter

# Function to get valid numeric input (Code128 can handle more flexible input)
def get_input_for_code128():
    while True:
        data = input("Enter alphanumeric input for Code128 (up to 20 chars): ")
        if data.isalnum() and 1 <= len(data) <= 20:
            return data
        else:
            print("Invalid input. Please enter 1-20 alphanumeric characters.")

try:
    # Create 'output' directory if it doesn't exist
    output_dir = os.path.join("output", "barcode")
    os.makedirs(output_dir, exist_ok=True)

    # Get data from the user
    input_data = get_input_for_code128()

    # Generate timestamped filename
    timestamp = datetime.datetime.now().strftime("%Y.%m.%d-%H.%M.%S")
    filename = f"code128_{timestamp}"

    # Generate Code128 barcode
    barcode_class = barcode.get_barcode_class('code128')
    barcode_obj = barcode_class(input_data, writer=ImageWriter())

    # Save barcode image
    output_path = os.path.join(output_dir, filename)
    barcode_obj.save(output_path)

    print(f"Code128 Barcode saved at '{output_path}.png'")

except Exception as e:
    print(f"An error occurred: {e}")
