#version 110

varying vec3 vertex_position;
varying vec3 vertex_normal;

uniform vec3 light_direction;
uniform vec3 ambient;
uniform vec3 diffuse;
uniform vec4 specular;

uniform float shininess;
uniform float alpha;

void
main (void)
{
  vec3 light_direction   = normalize(light_direction);
  vec3 vertex_position_n = normalize(vertex_position);

  // Ambient light component
  vec3 out_ambient = ambient;  

  // Diffuse light component
  float diffuse_factor = max(dot(light_direction, vertex_normal), 0.0);
  vec3 out_diffuse     = diffuse * diffuse_factor;  

  // Specular light component
  vec3 reflect_direction = reflect(vertex_position_n, vertex_normal);
  float amount_reflect   = dot(reflect_direction, light_direction);
  float specular_factor  = pow(max(amount_reflect, 0.0), shininess);
  vec3 out_specular      = specular.xyz * specular_factor;

  gl_FragColor = vec4(out_ambient + out_diffuse + out_specular, alpha);
}