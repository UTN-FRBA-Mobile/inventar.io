import datetime
import os
import qrcode

# Fixed prefix for identifying QR codes
PREFIX = "inv_jorge_el_curioso_"


# Function to get alphanumeric input (not a URL anymore)
def get_alphanumeric_input():
    while True:
        data = input("Enter string data: ")

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
        # Version 1-40, higher means more data can be stored
        version=10,
        # Error correction level, higher means more damage can be tolerated
        error_correction=qrcode.constants.ERROR_CORRECT_H,
        # Size of the box
        box_size=10,
        # Size of the outside border
        border=4,
    )

    qr.add_data(full_data)
    qr.make(fit=True)

    # Create an image from the QR Code instance
    # Colors are accepted as: '#RRGGBB', '(R, G, B)', or color names
    img = qr.make_image(fill_color="black", back_color='white')

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
