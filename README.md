# Plantlytics

![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Java](https://img.shields.io/badge/Java-F89820?style=for-the-badge&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Adobe XD](https://img.shields.io/badge/Adobe%20XD-FF61F6?style=for-the-badge&logo=adobexd&logoColor=white)
![Google Cloud](https://img.shields.io/badge/Google%20Cloud-4285F4?style=for-the-badge&logo=google-cloud&logoColor=white)
![TensorFlow](https://img.shields.io/badge/TensorFlow-FF6F00?style=for-the-badge&logo=TensorFlow&logoColor=white)

Welcome to the repository for the Plantlytics mobile app, a groundbreaking tool designed to provide fast, free, accurate, and convenient plant advice for everyone, including farmers, gardeners, and plant enthusiasts. Our Plant Disease Detector leverages advanced machine learning to identify plant diseases and offer helpful guidance.

## Features

- **Disease Identification**: Utilizes a sophisticated machine learning model to accurately identify various plant diseases.
- **High Accuracy**: Diagnoses plant illnesses with a remarkable average of 95% accuracy.
- **User-Friendly Interface**: Designed with simplicity in mind, making it accessible to all users regardless of their tech-savviness.
- **Instant Results**: Offers quick feedback, allowing for timely intervention and care for your plants.
- **Educational Resource**: Provides valuable information about plant diseases and their management.

## How It Works

The app uses a convolutional neural network (CNN) trained on a dataset of over 70,000 images encompassing 19 distinct plant types and 39 varieties of plant conditions. This model is integrated into the app as a Tensorflow Lite package, ensuring a seamless and efficient user experience. 

<div align="center">
  <video src="https://github.com/anniezhang2288/Plantlytics/assets/67795868/e65aa7ff-9ce6-4d4d-a229-f7822ce94fca" width="400" />
</div>
    
## Achievements

- [**Congressional App Challenge**](https://www.congressionalappchallenge.us/): Won 1st Place and was recognized by Congresswoman Judy Chu.
- [**IgniteCS Programming Expo**](https://ignitecsexpo.org/): Earned 3rd place in the Web/Mobile Applications category.
- **STEM Expo Presentation**: Secured an exclusive invitation to present to Congress members at the US Capitol Building, and featured on the House of Representatives website during the #HouseOfCode festival.
## Development

### Technologies Used

The Plantlytics app is built using a variety of powerful and efficient technologies:

- **Android Studio**: For setting up and managing the mobile app project.
- **Java**: The primary programming language used for developing the app.
- **Firebase**: To host individual user information and manage data storage.
- **Adobe XD**: For designing high-quality, engaging backgrounds and UI elements.
- **Google Cloud Platform/TensorFlow**: Used for training the machine learning model with a robust and scalable infrastructure.

### Machine Learning Model

Our CNN model was trained using Google's Cloud platform, leveraging a publicly available dataset. This approach ensured that our model is both accurate and reliable in identifying various plant diseases.

### Integration

After training, the model was converted into a Tensorflow Lite package for integration into the mobile app. This allows the app to run the model locally on a user's device, ensuring fast and efficient disease detection.

## Getting Started

To get started with the Plantlytics app:

1. **Clone the Repository**: Clone this repository to your local machine.
2. **Open in Android Studio**: Open the project in Android Studio.
3. **Run the App**: Build and run the app on an emulator or a physical device.




## Contributing

We welcome contributions to the Plantlytics app. If you have suggestions or improvements, feel free to fork this repository and submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE.md) - see the LICENSE file for details.

## Acknowledgments

- Thanks to the creators of the public dataset used for training our model.
- Appreciation to the Google Cloud Platform for providing the infrastructure for machine learning model training.
- Kudos to the Android Studio and Firebase teams for their excellent development tools.

---

For more information or support, please contact us at [anniezhang2288@berkeley.edu](mailto:anniezhang2288@berkeley.edu).
