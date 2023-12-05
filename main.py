from flask import Flask,render_template,redirect,url_for,request,jsonify,flash
from datetime import date
from requests import Session
from flask_sqlalchemy import SQLAlchemy
from flask_bootstrap import Bootstrap
import requests
from flask_login import UserMixin,login_user,LoginManager,login_required,current_user,logout_user
from werkzeug.security import generate_password_hash,check_password_hash
import json


app= Flask(__name__,template_folder="template")
app.app_context().push()
Bootstrap(app)
app.config['SQLALCHEMY_DATABASE_URI'] ='postgresql://postgres:helloworld@localhost/minor_db3'
db = SQLAlchemy(app)
app.secret_key = '4y8s7gf#kja&9$sl@2jx!p1oz2r3d5vu*bnq7mt8h0ic'


login_manager= LoginManager()
login_manager.login_view = 'sign_in'
login_manager.init_app(app)
movie_smallList = []
class User(db.Model,UserMixin):
    __tablename__="User"
    id= db.Column(db.Integer,primary_key=True)
    name=db.Column(db.String(100))
    email=db.Column(db.String(100))
    password=db.Column(db.String(10000))

db.create_all()


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))


    
@app.route('/welcome2',methods=('GET','POST'))
def welcomepage2():
    data_path_genres = 'genre_json_file.json'
    with open(data_path_genres, 'r') as file:
        genres = json.load(file)['genres']


    data_path_directors='directors._json_file2.json'
    with open(data_path_directors, 'r') as file:
        directors = json.load(file)['director']


    data_path_choreographer='choregrapher_json_file.json'
    with open(data_path_choreographer, 'r') as file:
        choreographers = json.load(file)['choregrapher']
        print(choreographers)

    data_path_keywords='keyword_json_file.json'
    with open(data_path_keywords, 'r') as file:
        keywords = json.load(file)['keyword']


    data_path_languages='language_json_file.json'
    with open(data_path_languages, 'r') as file:
        languages = json.load(file)['language']

    
    data_path_casts='cast_json_file2.json'
    with open(data_path_casts, 'r') as file:
        casts = json.load(file)['cast']


    
    data_path_countries='country_json_file.json'
    with open(data_path_countries, 'r') as file:
        countries = json.load(file)['country']



    



    


    # if request.method=="POST":
    #     data = {
    #         'genre': request.form.get('genre'),
    #         'language': request.form.get('language'),
    #         'country': request.form.get('country'),
    #         'keywords': request.form.get('keywords'),
    #         'actor': request.form.get('actor'),
    #         'director': request.form.get('director'),
    #         'choreographer': request.form.get('choreographer')
    #     }
    #     java_url = 'http://localhost:8080/receive_data'  # Replace with the Java application's URL
    #     try:
    #         response = requests.post(java_url, data=data)
    #         if response.status_code == 200:
    #             recommendations = response.json()
    #             return jsonify(recommendations)
    #         else:
    #             return jsonify({"error": "Failed to get recommendations from Java application"})
    #     except Exception as e:
    #         return jsonify({"error": "Error: " + str(e)})
    return render_template("index.html",genres=genres,directors=directors,choreographers=choreographers,keywords=keywords,countries=countries,languages=languages,casts=casts)


@app.route('/submit_form', methods=['POST'])
def submit_form():

    print("hello")

    data={
    'genre': request.form.get('genre'),
    'language': request.form.get('language'),
    'country': request.form.get('country'),
    'keyword': request.form.get('keyword'),
    'actor': request.form.get('actor'),
    'director': request.form.get('director'),
    'choreographer': request.form.get('choreographer')}
    print(data)
    java_url = 'http://localhost:8081/receive_data'  # Replace with the Java application's URL
    try:
        response = requests.post(java_url, data=data)
        print(movie_smallList)
        # print("response="+str(response.status_code))
        # if response.status_code == 200:
        #     recommendations = response.json()
        #     print("hey sana all good")
        #     return jsonify(recommendations)
        # else:
        #     return jsonify({"error": "Failed to get recommendations from Java application"})
    except Exception as e:
        return jsonify({"error": "Error: " + str(e)})
    response_data = {
        'message': 'Movie data sent successfully',
        'data': movie_smallList
    }

    # Use jsonify to return the response as JSON
    return jsonify(response_data), 201
    

    return jsonify({'message': 'Movie data sent successfully'}), 201

@app.route('/receive_processed_data2', methods=['POST'])
def receive_processed_data2():
    print("hello dutta ji")
    print(request.data)
    global movie_smallList 
    movies_data=request.data.decode('utf-8')
    movies_list = movies_data.split(", ")

# Extract the first 16 movies
    if len(movies_list) > 16:
      movie_smallList  = movies_list[:16]
    else:
         movie_smallList  = movies_list

    # Print the first 16 movies
    print("First 16 movies:")
    for movie in movie_smallList :
        print(movie)
    return jsonify({'message': 'Movie data sent successfully'}), 201


@app.route('/receive_processed_data3', methods=['POST'])
def receive_processed_data3():
    print("hello dutta ji3")
    print(request.data)
    global movie_smallList 
    movies_data=request.data.decode('utf-8')
    json_data = json.loads(movies_data)
    movies_list = json_data["recommendations"]

# Extract the first 16 movies
    if len(movies_list) > 16:
      movie_smallList  = movies_list[:16]

    else:
         movie_smallList  = movies_list

    # Print the first 16 movies
    print("First 16 movies:")
    for movie in movie_smallList :
        print(movie)
    return jsonify({'message': 'Movie data sent successfully'}), 201


       
     


@app.route('/receive_processed_data', methods=['POST'])
def receive_processed_data():
    print("hello dutta ji")
    print(request.data)

    data = request.data.decode('utf-8')  # Decode the incoming data as UTF-8

    # Split the incoming data into lines
    lines = data.split('\n')
    movie_list = []
    global movie_smallList 

    # Iterate through the lines and print movie titles
    for line in lines:
        if line.startswith("Recommended Movie (Relaxed Criteria):"):
            movie_title = line[len("Recommended Movie (Relaxed Criteria):"):].strip()
            print(movie_title)
            movie_list.append(movie_title)

    if len(movie_list) > 16:
        movie_smallList = movie_list[:16]
    else:
        movie_smallList = movie_list

    print(len(movie_list))
    print("movie_smallList")
    print(movie_smallList)
    
    return jsonify({'message': 'Movie data sent successfully'}), 201




@app.route('/login', methods=('GET', 'POST'))
def sign_in():
    if request.method == "POST":
        email = request.form.get("email")
        password = request.form.get("password")
        user = User.query.filter_by(email=email).first()
        try:
            print("hello girl")
            if not user:
                # flash("That email does not exist, please try again", "error")
                # return redirect(url_for("sign_in"))
                return jsonify({'message': 'Email doesnot exists '}), 400

            elif not check_password_hash(user.password, password):
                # flash("Password incorrect, please try again", "error")
                # return redirect(url_for("sign_in"))
                return jsonify({'message':'Password incorrect, please try again'}),202
            else:
                # If login is successful, redirect to welcomepage2
                login_user(user)
                print(current_user.name)
                return jsonify({'message': 'User registered successfully'}), 201
        
        except Exception as e:
            print('Error signing user:', str(e))
            return jsonify({'message': 'Internal server error'}), 500


    return render_template("sign_in.html")




@app.route('/register',methods=('GET', 'POST'))
def sign_up():
    if request.method=="POST":
        try:
            print("hello girl")

            if User.query.filter_by(email=request.form.get("email")).first():

                # flash("You have already signed up with that email,log in instead!","error")
                # return redirect(url_for("sign_up"))
                return jsonify({'message':'You have already signed up with that email,log in instead!'}),400
            


            hash_and_salted_password =generate_password_hash(request.form.get("password"),method='pbkdf2:sha256')

            new_user=User(email=request.form.get("email"),name=request.form.get("name"),password=hash_and_salted_password)
            db.session.add(new_user)
            db.session.commit()
            print("Its done ")
            login_user(new_user)
            return jsonify({'message':'User registered successfully'}),201
            
        except Exception as e:
            print('Error registering user:', str(e))
            return jsonify({'error': 'An error occurred while registering the user'}), 500  # Return a proper error response


    return render_template("sign_up.html")

# @app.route('/hello')
# def welcomepage3():
#     return render_template("sign_up.html")


@app.route('/my_page',methods=['GET', 'POST'])
def welcomepage5():
    user=current_user.name
    print(user)
    if request.method == 'POST':
        print("helloworld")
        search_term = request.form.get('search')
        java_url2= 'http://localhost:8082/content_filtering'
        try:
            response = requests.post(java_url2, data={'search': search_term})
        except Exception as e:
            return jsonify({"error": "Error: " + str(e)})
        
        
        response_data = {'result': 'Search term received: ' + search_term}
        movie_param = json.dumps(movie_smallList)
        return redirect(url_for('welcomepage5',data=movie_param))
    # Replace 'YOUR_API_KEY' with your TMDb API key
    api_key = '07f3ea839fd8f2f1fc4e47f81d91b51b'

    # Base URL for TMDb API
    base_url = 'https://api.themoviedb.org/3/'
    poster_base_url = 'https://image.tmdb.org/t/p/w500/'
    data_param = request.args.get('data')

    
    
    if data_param:
        # Decode and parse the data
        movie_titles = json.loads(data_param)
        # Create an empty list to store movie data
        movie_data_list = []

        # Iterate through the movie titles
        for movie_title in movie_titles:
            # Construct the URL for searching the movie by title
            url = f'{base_url}search/movie?api_key={api_key}&query={movie_title}'

            # Send the GET request
            response = requests.get(url)

            if response.status_code == 200:
                data = response.json()

                # Check if there are results
                if 'results' in data and data['results']:
                    # Get the first movie result (closest match)
                    movie_entry = data['results'][0]

                    # Extract data for the movie
                    title = movie_entry['title']
                    overview = movie_entry['overview']
                    poster_path = movie_entry['poster_path']
                    poster_url = poster_base_url + poster_path
                    
                    # Create a dictionary with the movie data
                    movie_data = {
                        'Title': title,
                        'Overview': overview,
                        'Poster Path': poster_url,
                    }

                    # Add the movie data to the list
                    movie_data_list.append(movie_data)
                else:
                    print(f"No matching movie found for title '{movie_title}'")
            else:
                # Handle the error for the movie search request
                print(f"Error for movie title '{movie_title}': {response.status_code} - {response.text}")
        
    return render_template("index6.html",movie_data_list=movie_data_list,user=user)

@app.route('/pagination',methods=['GET','POST'])
def pagination():
    
    picture=request.args.get('picture')
    title=request.args.get('title')
    description=request.args.get('description')

    if request.method=='POST':
        movie=request.form.get('movie')
        user= current_user.name
        rating=request.form.get('rating')
        # Load existing ratings from the JSON file
        with open('ratings.json', 'r') as file:
            ratings_data = json.load(file)

        # Check if the user already exists in the ratings data
        if user in ratings_data:
            # If the user exists, check if the movie already has a rating
            if movie in ratings_data[user]:
                # If the movie already has a rating, update it
                ratings_data[user][movie] = float(rating)
            else:
                # If the movie doesn't have a rating, add a new entry for the movie
                ratings_data[user][movie] = float(rating)
        else:
            # If the user doesn't exist, create a new entry for the user with the movie and rating
            ratings_data[user] = {movie: float(rating)}

        # Write the updated ratings data back to the JSON file
        with open('ratings.json', 'w') as file:
            json.dump(ratings_data, file, indent=2)
        
        return jsonify({'data update ':'data sent successfully'})
    return render_template('index7.html',picture=picture,title=title,description=description)


@app.route('/my_page2',methods=['GET', 'POST'])
def welcomepage6():
    user=current_user.name
    print(user)
    
    search_term = request.args.get('user')
    print("user name is:")
    print(search_term)
    java_url3= 'http://localhost:8083/collaborative_filtering'
    try:
        response = requests.post(java_url3, data={'search': search_term})
        movie_param = json.dumps(movie_smallList)
        return redirect(url_for('welcomepage5',data=movie_param))
    except Exception as e:
        return jsonify({"error": "Error: " + str(e)})


        


@app.route('/')
def welcomepage():
    return render_template("index2.html")




if __name__ == '__main__':
    app.run(debug=True)
    