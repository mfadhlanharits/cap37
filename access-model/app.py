from tensorflow import keras
from keras.preprocessing.image import load_img
from keras.preprocessing.image import img_to_array
from keras.models import load_model
from flask import Flask, request, json

app = Flask(__name__)

#Masih perlu bikin response buat ngririm hasil klasifikasi
@app.route('/hello', methods = ['GET'])
def hello():
    return 'Hello World!'


@app.route('/', methods = ['POST'])
def home():
    imagefile=request.files['file']
    
        
    img = load_image(imagefile)
    
    model = load_model('final_model.h5')
    
    result = model.predict_classes(img)

    if int(result[0]) == 0:
        res = "T-shirt/top"
    elif int(result[0]) == 1:
        res = "Trouser"
    elif int(result[0]) == 2:
        res = "Pullover"
    elif int(result[0]) == 3:
        res = "Dress"
    elif int(result[0]) == 4:
        res = "Coat"
    elif int(result[0]) == 5:
        res = "Sandal"
    elif int(result[0]) == 6:
        res = "Shirt"
    elif int(result[0]) == 7:
        res = "Sneaker"
    elif int(result[0]) == 8:
        res = "Bag"
    elif int(result[0]) == 9:
        res = "Ankle Boot"
    else:
        res = "No Categorized"

    response = app.response_class(
        response=json.dumps(res),
        status=200,
        mimetype='application/json'
        )
    return response

def load_image(filename):
    # load the image
    img = load_img(filename, grayscale=True, target_size=(28, 28))
    # convert to array
    img = img_to_array(img)
    # reshape into a single sample with 1 channel
    img = img.reshape(1, 28, 28, 1)
    # prepare pixel data
    img = img.astype('float32')
    img = img / 255.0
    return img


if __name__ == '__main__':
    app.run(debug=True)


