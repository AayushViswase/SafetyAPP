import firebase_admin
from firebase_admin import credentials, db
if not firebase_admin._apps:
    cred = credentials.Certificate({
        "type": "service_account",
        "project_id": "safetyapp-b7bc9",
        "private_key_id": "ea8f4cc02a24dfee72e1ec21ee3636c27043d889",
        "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEugIBADANBgkqhkiG9w0BAQEFAASCBKQwggSgAgEAAoIBAQDBPT0+DgkWWh8c\nk5P91rnGB6kfoFy/ZiTwAU+rjolSafZYVXRSnrxck2mg986hq7QzIeQyiQVMTUac\ngfTD5qm4HOhZGn3gjak5aQ7gFKJnS+pFgdB1iPUlliFJcLt38tNfJvIQDlcVSEwr\nymjhsGOfoBp1MRyhzvuEkGuSeBPIkY+r8q7TYh814XgXFC+fTfg5qFtM6CjkEeGc\nFZKY+lVDQyoRtEz7NbAosinmCwz6CsWfoyiNXKShlo2alS6X4fP6DcgYcPz5FkZN\nAIt3haQLcT/2M7BJyX9OpiOQbBB8+48wG5jTbElFbOSJ7B2c2mGmIvc63addWJpB\nz7sXeH8HAgMBAAECgf8MowJDnm52pABJHu3lwxMAtFSyftZnvmh7AMOid6yGJEgc\nMruU7/0ZZskdHL13FYxbuDd6S3i3h+iz6km6H6MayudmhgsrB9zG5/FeI9RrKKjq\nrcmodVJ/pioaXHJH8Gyb+4UTl5Al1fIgka//Q/9wydDOjd/lcaTGBRIBTA466Zw3\n6ps0ezKqA6hoPBUaP5hM46rHtYKGlFXODrz6QOqufoyyJHTu5ghOxR8BE+rWsr56\n+o1x4wdPUeAuIatPAc5ktVuWKXuTHYN0LrePIpaiVl9ifkXbNYrixkNszhMeOkkG\nJwCeJ52GO1ZRtfbeJ+vTIbp0sB5aJ2Mb+1g/xUkCgYEA/q3ycmWOcSjg6/ZJk0tr\n5VgQUbwyoQ8RcWPp01rJU5/6nmF8w1ZWksb4+xozdxLiD2TQh0WM4WcUIE9SL0ZM\n4G4gvdbY2KRD3S1bWtVj7ggUzQrlppGk9t/BHsRrLEbi3T1kXDITArT/bcivaz64\njrTYt1odtpo7pN+P6hD2ajUCgYEAwj29CjlStnPaLjEwS1up5jqFByg3y7uMUbzo\ncqgL6P3+uX5c7WZLFwXrQQu7FXLxBdmBQoQhkBwKT5pXN1MdGF7IvATCcOEyCdU2\nWl9lzg9FT9ThiyjpLseahA72wWKH5G0cN6DWj3SiRhb2VZlaFmIq3Y1jWAc7qCYx\nsLe6C8sCgYAr64eSHt1vumL/jlul4S3cQVGfJtt8VwLlERvz7QYnO8GWqq0/apZH\nfbTWaPBTKph9mx8zFNV00bfw0n0T1gXP9m4DDxgaVEm3zOiiQ4aWOCKOMcDk9t0C\nDhhZ2/m3iwn4qWuzUpdgsiISprdJwWlGgv2UsR/Kq9J5XmyNKxuzuQKBgBJ3ujx5\nnMKaGzlfFd9kVK/+Cl3W1g85wqqW0mbsUU2SsKBEQmOT4TnMJ0Tk76GnbwEGYALJ\n+0LvV4+f9gDdlxuVD3LI5Q/EaXSD4AoRneVMkGN5swP/HMSE180MeyuBybfPI+qq\nERxMk40ka8FRtj4AkWDFK5fYADUJhJnvrfGnAoGACi6oaX+hzCbcno3IngPR18qn\nwWK0WnJ0OHtXuI/kTp6ZwemrU1vZTTMR8FTuEEg9X3C9rRlhsiKDF4jH4cgSCDpJ\nmqPeIBAgEuXdL4tuODw+A02K5OA25vsq8++gTqb0iRitg3q3wRbzotKqVt6/K3iz\nNZFlalSgZ7AGbezbhuo=\n-----END PRIVATE KEY-----\n",
        "client_email": "firebase-adminsdk-1u3in@safetyapp-b7bc9.iam.gserviceaccount.com",
        "client_id": "103929442430543855623",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-1u3in%40safetyapp-b7bc9.iam.gserviceaccount.com"
    })
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://safetyapp-b7bc9-default-rtdb.firebaseio.com/'
    })

ref = db.reference("feedback/")

data=ref.get()
import numpy as np
from sklearn.linear_model import LinearRegression

# source = "Wakad"
# destination = "Magarpatta"
# time_period = "AM"
# time_interval = "06:00-09:00"

# Apply linear regression to matching data

def run_model(source,destination,time_period,time_interval):
    matching_data = [d for d in data['data'] if d['Source'] == source and d['Destination'] == destination ]
    if matching_data:
        X = np.array([[d['Road_Condition'], d['Public_Activities']] for d in matching_data])
        y_feels_safe = np.array([d['Feel_Safe'] for d in matching_data])
        reg_feels_safe = LinearRegression().fit(X, y_feels_safe)
        y_road_condition = np.array([d['Road_Condition'] for d in matching_data])
        reg_road_condition = LinearRegression().fit(X, y_road_condition)
        y_public_activities = np.array([d['Public_Activities'] for d in matching_data])
        reg_public_activities = LinearRegression().fit(X, y_public_activities)

        # Predict values for road_condition, public_activities, and feels_safe
        test_X = np.array([[3, 2]])  # Replace with test input from user
        predicted_feels_safe = reg_feels_safe.predict(test_X)
        predicted_road_condition = reg_road_condition.predict(test_X)
        predicted_public_activities = reg_public_activities.predict(test_X)

        return predicted_feels_safe, predicted_road_condition, predicted_public_activities

    else:
        noMatch="No matching data found."
        return 0