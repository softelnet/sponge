import image_classifier_service as service
import digits_model as model

if __name__ == '__main__':
    service.run_image_classifier_service(model.DigitsModel())
