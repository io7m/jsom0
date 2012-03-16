#version 110

varying vec3 vertex_position;
varying vec3 vertex_normal;
varying vec2 vertex_uv;

uniform vec3 light_direction;
uniform vec3 ambient;
uniform vec3 diffuse;
uniform vec4 specular;

uniform float shininess;
uniform float alpha;

uniform sampler2D texture;
uniform float texture_alpha;

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
  vec3 out_specular      = (specular.xyz * specular_factor) * specular.w;

  // Texture colour
  // The modelling program (Blender) produces UV coordinates that treat 0,0
  // as the top left of the image. OpenGL treats 0,0 as the bottom left. 
  vec4 out_texture       = texture2D(texture, vec2(vertex_uv.x, 1.0 - vertex_uv.y));

  gl_FragColor = vec4(out_ambient + out_diffuse + out_specular + (out_texture.rgb * texture_alpha), alpha);
}
