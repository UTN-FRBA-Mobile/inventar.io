import datetime
import os
import qrcode

# Fixed prefix for identifying QR codes
PREFIX = "test-prefix-123-"


# Function to get alphanumeric input (not a URL anymore)
def get_alphanumeric_input():
    while True:
        data = input("Enter alphanumeric data (letters and numbers only): ")

        # Check if data is empty
        if data:
            return data

        print("Input cannot be empty. Please try again.")


try:
    # Create 'output' directory if it doesn't exist
    output_dir = os.path.join("output", "qrcode")
    os.makedirs(output_dir, exist_ok=True)

    # Get alphanumeric data from the user
    user_data = get_alphanumeric_input()

    # Combine prefix with user data
    full_data = PREFIX + user_data

    # Generate QR code
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )

    qr.add_data(full_data)
    qr.make(fit=True)

    # Create an image from the QR Code instance
    img = qr.make_image(fill='black', back_color='white')

    # Generate a filename with timestamp
    timestamp = datetime.datetime.now().strftime("%Y.%m.%d-%H.%M.%S")
    filename = f"qrcode_{timestamp}.png"
    output_path = os.path.join(output_dir, filename)

    # Save the image
    img.save(output_path)
    print(f"QR Code with prefix '{PREFIX}' saved at '{output_path}'")

# Catch any exception, print it
except Exception as e:
    print(f"An error occurred: {e}")
